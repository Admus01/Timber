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
    first_name:                 str | None = None
    last_name:                  str | None = None
    email:                      str | None = None
    country_phone_code:         Optional[str]
    phone_number:               Optional[str]
    date_of_birth:              datetime.date | None = None
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
            raise HTTPException(status_code=500, detail=f"User insert failed: {str(E)}")
        else:
            return {"user_uuid": str(self.user_uuid)}

    def update_in_db(self, db_client, patch): #add patch
        try:
            items = [item for item in patch.values() if item is not None]
            items.append(str(self.user_uuid))
            # items = []
            # for item in patch.values():
            #     if item is not None:
            #         if not uuid_pattern.match(item):
            #             items.append(item)
            # items.append(str(self.user_uuid))
            # for key in patch:
            #     value = patch[key]
            #     if value is not None:
            #         if not uuid_pattern.match(value):
            #             keys.append(key)
            keys = [key for key in patch.keys() if key is not None]
            keys = tuple(keys)
            update_statement = User._prepare_update(keys)
            db_client.execute_with_params(
                update_statement,
                tuple(items)
            )
        except Exception as E:
            logger.warning(str(E))
            raise HTTPException(status_code=500, detail=f"update failed {str(E)}")
        else:
            return {"user_uuid": self.user_uuid}

    def read_from_db(self, db_client):
        select_statement = User._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([str(self.user_uuid)]))
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
            for position, header in enumerate(headers):
                self.__setattr__(header, converted_results[position])
            return True

    def delete_from_db(self, db_client):
        try:
            results = db_client.execute_with_params(
                User._prepare_delete(), tuple([str(self.user_uuid)])
            )
        except Exception as E:
            raise HTTPException(status_code=500, detail="Internal server error")
        else:
            return self.user_uuid

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
        return '''SELECT * FROM public.users WHERE user_uuid = %s'''

    def _prepare_delete():
        return('''DELETE FROM public.users WHERE user_uuid = %s;''')