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

class BookingPatch(BaseModel):
    booked_from:                Optional[str]
    booked_till:                Optional[str]

class Booking(BaseModel):
    location_uuid:              UUID
    booking_uuid:               UUID = Field(default_factory=uuid4)
    booked_user_uuid:           UUID
    booked_from:                str
    booked_till:                str

    def serialize_for_db(self):
        attribute_names = [item for item in self.dict().keys() if getattr(self, item) is not None]
        attribute_values = tuple([
            str(getattr(self, item)) if isinstance(getattr(self, item), UUID)
            else getattr(self, item)
            for item in self.dict().keys() if getattr(self, item) is not None
        ])
        return attribute_names, attribute_values

        # - CRUD operations - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def store_in_db(self, db_client):
        attribute_names, attribute_values = self.serialize_for_db()
        insert_statement = Booking._prepare_insert(attribute_names)
        try:
            db_client.execute_with_params(
                insert_statement,
                attribute_values
            )
        except Exception as E:
            logger.error(str(E))
            raise HTTPException(status_code=500, detail=f"Booking insert failed: {str(E)}")
        else:
            return {"booking_uuid": str(self.booking_uuid)}

    def update_in_db(self, db_client, patch):
        try:
            update_statement = Booking._prepare_update(key for key, value in patch.items() if value is not None)
            items = [item for item in patch.values() if item is not None]
            items.append(str(self.booking_uuid))

            db_client.execute_with_params(
                update_statement,
                tuple(items)
            )
            # self.read_from_db(db_client)
        except Exception as E:
            logger.warning(str(E))
            raise HTTPException(status_code=500, detail=f"update failed {str(E)}")
        else:
            return {"booking_uuid": str(self.booking_uuid)}

    def read_from_db(self, db_client):
        select_statement = Booking._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([str(self.booking_uuid)]))
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        if results == []:
            raise HTTPException(status_code=404, detail="Booking not found")
        else:
            converted_results = [
                item.strftime("%Y-%m-%d %H:%M:%S") if isinstance(item, datetime.datetime)
                else item
                for item in results[0]
            ]
            for position, header in enumerate(headers):
                self.__setattr__(header, converted_results[position])
            return True

        # - Booking login data patch - - - - - - - - - - - - - - - - - - - - - -

    def instantitate_booking_from_db(booking_uuid, db_client):
        select_statement = Booking._prepare_select()
        # try:
        #     headers, results = db_client.query_with_params_headers(select_statement, tuple([location_uuid]))
        # except Exception as E:
        #     raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        # if results == []:
        #     raise HTTPException(status_code=404, detail="Location not found")
        # else:
        #     converted_results = [
        #         item.strftime("%Y-%m-%d %H:%M:%S") if isinstance(item, datetime.datetime)
        #         else item
        #         for item in results[0]
        #     ]
        #     with open("file.txt", "w") as file:
        #         file.write(str(headers))
        #         file.write(str(converted_results))
        #     location = Location(**dict(zip(headers, converted_results)))
        results = db_client.query_with_params_headers(select_statement, tuple([booking_uuid]))[1][0][0]
        with open("file.txt", "w") as file:
            file.write(str(results))
        booking = Booking(**results)
        return booking

        # - SQL statements - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def _prepare_insert(attribute_names):
        statement = '''
                INSERT INTO public.bookings({schema})
                VALUES ({place_holders});
                '''.format(
            schema=', '.join(attribute_names),
            place_holders=', '.join(['%s'] * len(attribute_names))
        )
        return statement

    def _prepare_select():
        return '''SELECT get_booking_data(%s);'''

    def _prepare_delete():
        return '''DELETE FROM public.bookings WHERE booking_uuid = %s;'''

    def _prepare_update(attribute_names):
        statement = '''
                   UPDATE public.bookings
                   SET
                   {attribute_place_holders}
                   WHERE booking_uuid = %s;
                   '''.format(
            attribute_place_holders=', '.join([attribute + " = %s" for attribute in attribute_names])
        )
        return statement

