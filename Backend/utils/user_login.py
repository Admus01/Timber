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

class UserLoginPatch(BaseModel):
    email:                  Optional[str]
    username:               Optional[str]
    hashed_psw:             Optional[str]

class UserLogin(BaseModel):
    user_uuid:              UUID
    email:                  str
    username:               str
    salt:                   Optional[str]
    hashed_psw:             Optional[str]
    created_on:             Optional[str]
    modified_on:            Optional[str]

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
        insert_statement = UserLogin._prepare_insert(attribute_names)
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

    def get_salt(self, user_uuid, db_client):
        try:
            salt = db_client.query(
                f"select salt from login_data where user_uuid = '{user_uuid}' "
            )
        except Exception as E:
            logger.error(str(E))
            raise HTTPException(status_code=500, detail=f"Company insert failed: {str(E)}")
        else:
            return salt

    def update_in_db(self, db_client, patch):
        try:
            update_statement = UserLogin._prepare_update(key for key, value in patch.items() if value is not None)
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
            return {"user_uuid": self.user_uuid}

    def read_from_db(self, db_client):
        select_statement = UserLogin._prepare_select()
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


# - User login data patch - - - - - - - - - - - - - - - - - - - - - -
    def instantitate_user_from_db(user_uuid, db_client):
        select_statement = UserLogin._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([user_uuid]))
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        if results == {}:
            raise HTTPException(status_code=404, detail="User not found")
        else:
            user = UserLogin(**dict(zip(headers, results)))
            return user

# - SQL statements - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def _prepare_insert(attribute_names):
        statement = '''
            INSERT INTO public.login_data({schema})
            VALUES ({place_holders});
            '''.format(
            schema=', '.join(attribute_names),
            place_holders=', '.join(['%s'] * len(attribute_names))
        )
        return statement

    def _prepare_select():
        return '''SELECT * FROM select_user_login_by_uuid(%s)'''

    def _prepare_delete():
        return '''DELETE FROM public.login_data WHERE user_uuid = %s;'''

    def _prepare_update(attribute_names):
        statement = '''
               UPDATE public.login_data
               SET
               {attribute_place_holders}
               WHERE user_uuid = %s;
               '''.format(
            attribute_place_holders=', '.join([attribute + " = %s" for attribute in attribute_names])
        )
        return statement

