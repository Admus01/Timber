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

class LocationPatch(BaseModel):
    name:                       Optional[str]
    beds:                       Optional[int]
    description:                Optional[str]
    image1:                     Optional[str]
    image2:                     Optional[str]
    image3:                     Optional[str]
    image4:                     Optional[str]
    image5:                     Optional[str]
    image6:                     Optional[str]
    image7:                     Optional[str]
    image8:                     Optional[str]
    image9:                     Optional[str]
    image10:                    Optional[str]
class Location(BaseModel):
    location_uuid:              UUID = Field(default_factory=uuid4) # str = str(uuid4())# str = str(uuid4())
    user_uuid:                  UUID
    name:                       str | None = None
    beds:                       int | None = None
    description:                str | None = None
    address_city:               str | None = None
    address_street:             str | None = None
    address_apartment_number:   Optional[str]
    address_state:              Optional[str]
    address_country:            str | None = None
    image1:                     str | None = None
    image2:                     Optional[str]
    image3:                     Optional[str]
    image4:                     Optional[str]
    image5:                     Optional[str]
    image6:                     Optional[str]
    image7:                     Optional[str]
    image8:                     Optional[str]
    image9:                     Optional[str]
    image10:                    Optional[str]
    is_active:                  bool | None = None
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
        insert_statement = Location._prepare_insert(attribute_names)
        try:
            db_client.execute_with_params(
                insert_statement,
                attribute_values
            )
        except Exception as E:
            logger.error(str(E))
            raise HTTPException(status_code=500, detail=f"Location insert failed: {str(E)}")
        else:
            return {"location_uuid": str(self.location_uuid)}

    def update_in_db(self, db_client, patch):
        try:
            update_statement = Location._prepare_update(key for key, value in patch.items() if value is not None)
            items = [item for item in patch.values() if item is not None]
            items.append(str(self.location_uuid))

            db_client.execute_with_params(
                update_statement,
                tuple(items)
            )
            # self.read_from_db(db_client)
        except Exception as E:
            logger.warning(str(E))
            raise HTTPException(status_code=500, detail=f"update failed {str(E)}")
        else:
            return({"location_uuid": self.location_uuid})

    def read_from_db(self, db_client):
        select_statement = Location._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([str(self.location_uuid)]))
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        if results == []:
            raise HTTPException(status_code=404, detail="Location not found")
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
                Location._prepare_delete(), tuple([str(self.location_uuid)])
            )
        except Exception as E:
            raise HTTPException(status_code=500, detail="Internal server error")
        else:
            return results

# - Location login data patch - - - - - - - - - - - - - - - - - - - - - -
    def instantitate_location_from_db(location_uuid, db_client):
        select_statement = Location._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([location_uuid]))
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        if results == []:
            raise HTTPException(status_code=404, detail="Location not found")
        else:
            converted_results = [
                item.strftime("%Y-%m-%d %H:%M:%S") if isinstance(item, datetime.datetime)
                else item
                for item in results[0]
            ]
            with open("file.txt", "w") as file:
                file.write(str(headers))
                file.write(str(converted_results))
            location = Location(**dict(zip(headers, converted_results)))
        # results = db_client.query_with_params_headers(select_statement, tuple([location_uuid]))
        # location = Location(**results)
        return location

# - SQL statements - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def _prepare_insert(attribute_names):
        statement = '''
            INSERT INTO public.locations({schema})
            VALUES ({place_holders});
            '''.format(
            schema=', '.join(attribute_names), 
            place_holders=', '.join(['%s'] * len(attribute_names))
        )
        return statement

    def _prepare_select():
        # return '''SELECT get_location_data(%s);'''
        return f"SELECT * FROM public.locations where location_uuid = %s"

    def _prepare_delete():
        return '''DELETE FROM public.locations WHERE location_uuid = %s;'''

    def _prepare_update(attribute_names):
        statement = '''
               UPDATE public.locations
               SET
               {attribute_place_holders}
               WHERE location_uuid = %s;
               '''.format(
            attribute_place_holders=', '.join([attribute + " = %s" for attribute in attribute_names])
        )
        return statement

