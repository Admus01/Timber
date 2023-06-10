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

    def serialize_for_db(self, data_json):
        attribute_names     = [item for item in data_json.dict().keys() if getattr(data_json, item) is not None]
        attribute_values    = tuple([
            str(getattr(data_json, item)) if isinstance(getattr(data_json, item), UUID)
            else getattr(data_json, item)
            for item in data_json.dict().keys() if getattr(data_json, item) is not None
        ])
        return attribute_names, attribute_values


# - CRUD operations - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def store_in_db(self, attribute_names, attribute_values, db_client):
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