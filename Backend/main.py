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

# db_config = {
#     "database": "postgres",
#     "host":     "localhost",
#     "port":     "5432",
#     "password": "",
#     "user":     "postgres",
#     "sslmode":  "prefer",
# }

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

# get user data
@app.get("/user/{user_uuid}")
async def get_user_by_id(user_uuid):
    user_data = db_client.query(f"SELECT * FROM public.select_user_by_uuid('{user_uuid}')")
    return user_data[0][0]


#register
@app.post("/register")
async def register(user_data: User):
    user_data.store_in_db(db_client)
    return user_data.user_uuid

#set login data
@app.post("/login_data")
async def set_login_data(user_login_data: UserLogin):
    user_login_data.store_in_db(db_client)
    salt = user_login_data.get_salt(user_login_data.user_uuid, db_client)
    return salt

#patch login data
@app.patch("/update_login_data/{user_uuid}")
async def patch_login_data(user_uuid, user_login_data_patch: UserLoginPatch):
    target_user = UserLogin.instantitate_user_from_db(user_uuid, db_client)
    return target_user.user_uuid

#patch user data
@app.patch("/update_user_data/{user_uuid}")
async def patch_user_data(user_uuid, user_data_patch: UserPatch):
    target_user = User.instantitate_user_from_db(user_uuid, db_client)
    target_user.update_in_db(db_client, user_data_patch.dict(exclude_unset=True))
    return user_uuid
    # update_user_data = user_data_patch.dict(exclude_unset=True)
    # updated_user_data = target_user.copy(update=update_user_data).dict()
    # target_user.update_in_db(db_client, updated_user_data)
    # return updated_user_data


# login
@app.get("/login/{user_email}")
async def get_salt_by_user_name(user_email):
    salt = db_client.query(f"select salt from public.login_data where email = '{user_email}'")
    hashed_psw = db_client.query(f"select hashed_psw from public.login_data where email = '{user_email}'")
    user_uuid = db_client.query(f"select user_uuid from public.login_data where email = '{user_email}'")
    return salt, hashed_psw, user_uuid

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
@app.patch("/location/{location_uuid}")
async def patch_location_data(location_uuid, location_patch: LocationPatch):
    target_location = Location.instantitate_location_from_db(location_uuid, db_client)
    target_location.update_in_db(db_client, location_patch.dict())
    return location_uuid

