import datetime
import logging
import psycopg2
import io

from fastapi    import HTTPException
from pydantic   import BaseModel, Field
from typing     import Optional
from uuid       import uuid4, UUID

logger = logging.getLogger()
logger.setLevel(logging.INFO)

class UserPatch(BaseModel):
    first_name:                 Optional[str]
    last_name:                  Optional[str]
    email:                      Optional[str]
    country_phone_code:         Optional[str]
    phone_number:               Optional[str]
    citizenship:                Optional[str]
    modified_on:                Optional[str]


class User(BaseModel):
    user_uuid:                  UUID = Field(default_factory=uuid4) # str = str(uuid4())# str = str(uuid4())
    first_name:                 str
    last_name:                  str
    email:                      str
    country_phone_code:         Optional[str]
    phone_number:               Optional[str]
    date_of_birth:              datetime.date
    citizenship:                Optional[str]
    created_on:                 Optional[str]
    modified_on:                Optional[str]

    def serialize_for_db(self):
        attribute_names     = [item for item in self.dict().keys() if getattr(self, item) is not None]
        attribute_values    = tuple([
            str(getattr(self, item)) if isinstance(getattr(self, item), UUID)
            else getattr(self, item)
            for item in self.dict().keys() if getattr(self, item) is not None
        ])
        return attribute_names, attribute_values


# - CRUD operations - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def store_in_db(self, db_client):
        attribute_names, attribute_values = self.serialize_for_db()
        insert_statement = User._prepare_insert(attribute_names)
        try:
            db_client.execute_with_params(
                insert_statement,
                attribute_values
            )
        except Exception as E:
            logger.error(str(E))
            raise HTTPException(status_code=500, detail=f"Company insert failed: {str(E)}")
        else:
            return {"user_uuid": str(self.user_uuid)}

    def update_in_db(self, db_client, patch):
        try:
            update_statement = User._prepare_update(key for key, value in patch.items() if value is not None)
            items = [item for item in patch.values() if item is not None]
            items.append(str(self.user_uuid))

            db_client.execute_with_params(
                update_statement,
                tuple(items)
            )
            self.read_from_db(db_client)
        except Exception as E:
            logger.warning(str(E))
            raise HTTPException(status_code=500, detail=f"update failed {str(E)}")
        else:
            return({"user_uuid": self.user_uuid})

    # - User login data patch - - - - - - - - - - - - - - - - - - - - - -
    def instantitate_user_from_db(user_uuid, db_client):
        select_statement = User._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([user_uuid]))
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        if results == []:
            raise HTTPException(status_code=404, detail="User not found")
        else:
            converted_results = [
                item.strftime("%Y-%m-%d %H:%M:%S") if isinstance(item, datetime.datetime)
                else item
                for item in results[0]
            ]
            user = User(**dict(zip(headers, converted_results)))
            return user

# - SQL statements - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def _prepare_insert(attribute_names):
        statement = '''
            INSERT INTO public.users({schema})
            VALUES ({place_holders});
            '''.format(
            schema=', '.join(attribute_names),
            place_holders=', '.join(['%s'] * len(attribute_names))
        )
        return statement

    def _prepare_update(attribute_names):
        statement = '''
               UPDATE public.users
               SET
               {attribute_place_holders}
               WHERE user_uuid = %s;
               '''.format(
            attribute_place_holders=', '.join([attribute + " = %s" for attribute in attribute_names])
        )
        return statement


    def _prepare_select():
        return '''SELECT * FROM select_user_by_uuid(%s)'''