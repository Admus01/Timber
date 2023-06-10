import logging
import os
import uvicorn
import dotenv

from fastapi import FastAPI, HTTPException, Response, BackgroundTasks
from database.db import Database

from utils.user import User


app = FastAPI()

dotenv.load_dotenv('.env')

db_config = {
    "database": os.environ.get('database'),
    "host":     os.environ.get('host'),
    "port":     os.environ.get('port'),
    "password": os.environ.get('password'),
    "user":     os.environ.get('user'),
    "sslmode":  os.environ.get('sslmode'),
}

db_client = Database(db_config)

data = {
    "first_name":           "Adam",
    "last_name":            "Hanusek",
    "email":                "hanusek.adam@gmail.com",
    "country_phone_code":   "420",
    "phone_number":         "606556984",
    "date_of_birth":        "2005-03-09",
    "citizenship":          "Czech"
}

@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}
@app.get("/user/{user_id}")
async def get_user_by_id(user_id):
    user_data = db_client.query(f"select * from public.users where user_id = {user_id}")
    return user_data


#register
@app.post("/register/{data}")
async def register(data, user_data: User):
    # attribute_names, attribute_values = user_data.serialize_for_db(data)
    # return attribute_names, attribute_values
    # user_data.store_in_db(attribute_names, attribute_values, db_client)
    # return user_data.user_uuid
    return user_data, data




#login
# @app.get("/login/{user_name}")
# async def get_salt_by_user_name(user_name):

#
# from fastapi import FastAPI
# from pydantic import BaseModel
#
#
# class Item(BaseModel):
#     name: str
#     description: str | None = None
#     price: float
#     tax: float | None = None
#
#
# app = FastAPI()
#
#
# @app.post("/items/")
# async def create_item(item: Item):
#     return item