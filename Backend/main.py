import logging
import os
import dotenv

from fastapi import FastAPI, HTTPException, Response, BackgroundTasks
from database.db import Database
from utils.location import Location, LocationPatch

from utils.user import User, UserPatch
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

# - User methods - - - - - - - - - - - - - - - - - -

# register
@app.post("/register")
async def register(user_data: User):
    user_data.store_in_db(db_client)
    return {"user_uuid":user_data.user_uuid}

# set login data
@app.post("/login_data")
async def set_login_data(user_login_data: UserLogin):
    user_login_data.store_in_db(db_client)
    salt = user_login_data.get_salt(user_login_data.user_uuid, db_client)
    return {"salt" : salt}

# patch login data
@app.patch("/update_login/{user_uuid}")
async def patch_login_data(user_uuid, user_login_data_patch: UserLoginPatch):
    target_user = UserLogin.instantitate_user_from_db(user_uuid, db_client)
    target_user.update_in_db(db_client, user_login_data_patch.dict(exclude_unset=True))
    return {"user_uuid":target_user.user_uuid}

# delete login data
@app.delete("/delete_login/{user_uuid}")
async def delete_user_data(user_uuid):
    target_user = UserLogin.instantitate_user_from_db(user_uuid, db_client)
    result = target_user.delete_from_db(db_client)
    return result


# login
# @app.post("/login")
# async def login(user_login: UserLoginPatch):
#     result = UserLogin.login(db_client, user_login)
#     return result


# get user data
@app.get("/user/{user_uuid}")
async def get_user_by_id(user_uuid):
    user_data = db_client.query(f"SELECT get_user_data('{user_uuid}')")
    return user_data[0][0][0]


# patch user data
@app.patch("/update_user/{user_uuid}")
async def patch_user_data(user_uuid, user_data_patch: UserPatch):
    target_user = User.instantitate_user_from_db(user_uuid, db_client)
    target_user.update_in_db(db_client, user_data_patch.dict(exclude_unset=True))
    return {"user_uuid":user_uuid}

# delete user data
@app.delete("/delete_user/{user_uuid}")
async def delete_user_data(user_uuid):
    target_user = User.instantitate_user_from_db(user_uuid, db_client)
    result = target_user.delete_from_db(db_client)
    return {"user_uuid":result}


# - Location functions - - - - - - - - - - - - - - -

# create location
@app.post("/create_location")
async def add_location(location_data: Location):
    location_data.store_in_db(db_client)
    return location_data.location_uuid

# get location information
@app.get("/location/{location_uuid}")
async def get_location_data(location_uuid):
    location_data = db_client.query(f"SELECT * FROM select_location_by_uuid('{location_uuid}')")
    return location_data[0][0]

# patch location data
@app.patch("/update_location/{location_uuid}")
async def patch_location_data(location_uuid, location_patch: LocationPatch):
    target_location = Location.instantitate_location_from_db(location_uuid, db_client)
    target_location.update_in_db(db_client, location_patch.dict(exclude_unset=True))
    return location_uuid

