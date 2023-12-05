import datetime
import logging
import psycopg2
import io

from fastapi import HTTPException
from pydantic import BaseModel, Field
from typing import Optional
from uuid import uuid4, UUID

logger = logging.getLogger()
logger.setLevel(logging.INFO)


class ReviewPatch(BaseModel):
    title:                      Optional[str]
    review:                     Optional[str]
    rating:                     Optional[float]


class Review(BaseModel):
    review_uuid:                UUID = Field(default_factory=uuid4)  # str = str(uuid4())# str = str(uuid4())
    location_uuid:              UUID
    user_uuid:                  UUID
    title:                      str
    review:                     str
    rating:                     float
    created_on:                 Optional[str]
    modified_on:                Optional[str]

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
        insert_statement = Review._prepare_insert(attribute_names)
        try:
            db_client.execute_with_params(
                insert_statement,
                attribute_values
            )
        except Exception as E:
            logger.error(str(E))
            raise HTTPException(status_code=500, detail=f"Review insert failed: {str(E)}")
        else:
            return {"review_uuid": str(self.review_uuid)}

    def update_in_db(self, db_client, patch):
        try:
            update_statement = Review._prepare_update(key for key, value in patch.items() if value is not None)
            items = [item for item in patch.values() if item is not None]
            items.append(str(self.review_uuid))

            db_client.execute_with_params(
                update_statement,
                tuple(items)
            )
            # self.read_from_db(db_client)
        except Exception as E:
            logger.warning(str(E))
            raise HTTPException(status_code=500, detail=f"update failed {str(E)}")
        else:
            return ({"review.uuid": self.review_uuid})

    def read_from_db(self, db_client):
        select_statement = Review._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([str(self.review_uuid)]))
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        if results == []:
            raise HTTPException(status_code=404, detail="Review not found")
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
                Review._prepare_delete(), tuple([str(self.review_uuid)])
            )
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {E}")
        else:
            return results

    # - Review login data patch - - - - - - - - - - - - - - - - - - - - - -
    def instantitate_review_from_db(review_uuid, db_client):
        select_statement = Review._prepare_select()
        try:
            headers, results = db_client.query_with_params_headers(select_statement, tuple([review_uuid]))
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")
        if results == []:
            raise HTTPException(status_code=404, detail="Review not found")
        else:
            converted_results = [
                item.strftime("%Y-%m-%d %H:%M:%S") if isinstance(item, datetime.datetime)
                else item
                for item in results[0]
            ]
            review = Review(**dict(zip(headers, converted_results)))
        # results = db_client.query_with_params_headers(select_statement, tuple([review.uuid]))
        # Review = Review(**results)
        return review


    # - Get reviews - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def get_reviews(location_uuid, page_index, order_by, db_client):
        max_page_values = 10
        if order_by == "latest":
            statement = f"SELECT get_reviews_by_latest('{location_uuid}', {page_index}, {max_page_values})"

        elif order_by == "best":
            statement = f"SELECT get_reviews_by_best('{location_uuid}', {page_index}, {max_page_values})"

        elif order_by == "worst":
            statement = f"SELECT get_reviews_by_worst('{location_uuid}', {page_index}, {max_page_values})"

        try:
            return db_client.query(statement)[0][0]
        except Exception as E:
            raise HTTPException(status_code=500, detail=f"Internal server error {str(E)}")


    # - SQL statements - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    def _prepare_insert(attribute_names):
        statement = '''
            INSERT INTO public.reviews({schema})
            VALUES ({place_holders});
            '''.format(
            schema=', '.join(attribute_names),
            place_holders=', '.join(['%s'] * len(attribute_names))
        )
        return statement

    def _prepare_select():
        # return '''SELECT get_Review_data(%s);'''
        return f"SELECT * FROM public.reviews where review_uuid = %s"

    def _prepare_delete():
        return '''DELETE FROM public.reviews WHERE review_uuid = %s;'''

    def _prepare_update(attribute_names):
        statement = '''
               UPDATE public.reviews
               SET
               {attribute_place_holders}
               WHERE review_uuid = %s;
               '''.format(
            attribute_place_holders=', '.join([attribute + " = %s" for attribute in attribute_names])
        )
        return statement


