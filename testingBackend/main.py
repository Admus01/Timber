# from fastapi import FastAPI
# from fastapi.encoders import jsonable_encoder
# from pydantic import BaseModel
#
# app = FastAPI()
#
#
# class Item(BaseModel):
#     name: str | None = None
#     description: str | None = None
#     price: float | None = None
#     tax: float = 10.5
#     tags: list[str] = []
#
#
# items = {
#     "foo": {"name": "Foo", "price": 50.2},
#     "bar": {"name": "Bar", "description": "The bartenders", "price": 62, "tax": 20.2},
#     "baz": {"name": "Baz", "description": None, "price": 50.2, "tax": 10.5, "tags": []},
# }
#
#
# @app.get("/items/{item_id}", response_model=Item)
# async def read_item(item_id: str):
#     return items[item_id]
#
#
# @app.patch("/items/{item_id}", response_model=Item)
# async def update_item(item_id: str, item: Item):
#     stored_item_data = items[item_id]
#     stored_item_model = Item(**stored_item_data)
#     update_data = item.dict(exclude_unset=True)
#     updated_item = stored_item_model.copy(update=update_data)
#     items[item_id] = jsonable_encoder(updated_item)
#     return updated_item
import re
from uuid import UUID

uuid = "7930a152-6a50-4972-9f2a-ae634ceacd52"
patch = {
  "user_uuid": "6b1b9efe-a77e-4de5-961f-a2a56118b2d5",
  "first_name": "negr",
  "last_name": None,
  "email": None,
  "country_phone_code": "69",
  "phone_number": "string",
  "date_of_birth": None,
  "citizenship": "somalsko",
  "created_on": None,
  "modified_on": "2023-07-17 10:25:09.941984"
}
uuid_pattern = re.compile(r'^[a-f0-9]{8}-[a-f0-9]{4}-[1-5][a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$')
# items = [item for item in patch.items() if item is not None or not uuid_pattern.match(item)]
items = []
for item in patch.values():
    if item is not None:
        if not uuid_pattern.match(item):
            items.append(item)
        else:
            items.append(str(item))
print(items)


# key = (key for key, value in patch.items() if value is not None)
keys = []
for key in patch:
    value = patch[key]
    if value is not None:
        if not uuid_pattern.match(value):
            keys.append(key)
keys = tuple(keys)
print(keys)