import logging
import os
import dotenv

from fastapi import FastAPI, HTTPException, Response, BackgroundTasks
from database.db import Database

from utils.user import User
from utils.user_login import UserLogin, UserLoginPatch


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


@app.post("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}
@app.get("/user/{user_id}")
async def get_user_by_id(user_id):
    user_data = db_client.query(f"select * from public.users where user_id = {user_id}")
    return user_data


#register
@app.post("/register/{data}")
async def register(user_data: User):
    user_data.store_in_db(db_client)
    return user_data.user_uuid

#set login data
@app.post("/login_data/")
async def set_login_data(user_login_data: UserLogin):
    user_login_data.store_in_db(db_client)
    salt = user_login_data.get_salt(user_login_data.user_uuid, db_client)
    return salt

#patch login data
@app.patch("/update_psw/{user_uuid}")
async def patch_login_data(user_uuid, user_login_data_patch: UserLoginPatch):
    target_user = UserLogin.instantitate_user_from_db(user_uuid, db_client)
    target_user.update_in_db(db_client, user_login_data_patch.dict())
    return target_user.user_uuid


# login
@app.get("/login/{user_uuid}")
async def get_salt_by_user_name(user_uuid):
    salt = db_client.query(f"select salt from public.login_data where user_uuid = '{user_uuid}'")
    hashed_psw = db_client.query(f"select hashed_psw from public.login_data where user_uuid = '{user_uuid}'")
    return salt, hashed_psw