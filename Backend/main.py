import logging
import os
import dotenv

from fastapi import FastAPI, HTTPException, Response, BackgroundTasks, Header, Request

from google.oauth2 import id_token
from google.auth.transport import requests

from database.db import Database

from utils.location import Location, LocationPatch
from utils.booking import Booking, BookingPatch
from utils.user import User, UserPatch
# from utils.user_login import UserLogin, UserLoginPatch


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

web_client_id = os.environ.get('web_client_id')
android_client_id = os.environ.get('android_client_id')

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

# # Validate email
# @app.get("/validate_email/{user_email}")
# async def validate_email(user_email):
#     response = db_client.query(f"SELECT * FROM users WHERE email = '{user_email}'")
#     if len(response) == 0: # empty list == email not found
#         return False
#     else: # list has field == email found
#         return True

# register
@app.post("/register")
async def register(user_data: User):
    user_data.store_in_db(db_client)
    return {"user_uuid":user_data.user_uuid}

# set login data
# @app.post("/login_data")
# async def set_login_data(user_login_data: UserLogin):
#     user_login_data.store_in_db(db_client)
#     salt = user_login_data.get_salt(user_login_data.user_uuid, db_client)
#     return {"salt" : salt}
#
# patch login data
# @app.patch("/update_login/{user_uuid}")
# async def patch_login_data(user_uuid, user_login_data_patch: UserLoginPatch):
#     target_user = UserLogin.instantitate_user_from_db(user_uuid, db_client)
#     target_user.update_in_db(db_client, user_login_data_patch.dict(exclude_unset=True))
#     return {"user_uuid":target_user.user_uuid}
#
# delete login data
# @app.delete("/delete_login/{user_uuid}")
# async def delete_user_data(user_uuid):
#     target_user = UserLogin.instantitate_user_from_db(user_uuid, db_client)
#     result = target_user.delete_from_db(db_client)
#     return result


# login
@app.post("/login")
async def login(user_login: Request):
    berear = user_login.headers.get("Authorization")
    response = {"user_uuid": db_client.query(f"SELECT user_uuid FROM users WHERE berear = '{berear}'")[0][0]}
    return response

# validate client
@app.get("/validate_client/{token}")
async def validate_client(token):
    try:
        idinfo = id_token.verify_oauth2_token(token, requests.Request())
        if idinfo['aud'] not in [web_client_id, android_client_id]:
            raise ValueError('Could not verify audience.')

        # ID token is valid. Get the user's Google Account ID from the decoded token.
        userid = idinfo['sub']
        return userid
    except ValueError:
        # Invalid token
        return False

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
   return location_data.store_in_db(db_client)

# get location information
@app.get("/location/{location_uuid}")
async def get_location_data(location_uuid):
    location_data = db_client.query(f"SELECT get_location_data('{location_uuid}')")
    # location_data = db_client.query(f"SELECT * FROM locations WHERE location_uuid = '{location_uuid}'")
    return location_data[0][0][0]

# patch location data
@app.patch("/update_location/{location_uuid}")
async def patch_location_data(location_uuid, location_patch: LocationPatch):
    target_location = Location.instantitate_location_from_db(location_uuid, db_client)
    return target_location.update_in_db(db_client, location_patch.dict(exclude_unset=True))

# delete location
@app.delete("/delete_location/{location_uuid}")
async def delete_location(location_uuid):
    target_location = Location.instantitate_location_from_db(location_uuid, db_client)
    return target_location.delete_from_db(db_client)

# - Bookings functions - - - - - - - - - - - - - - -

# create booking
@app.post("/create_booking")
async def create_booking(booking_data: Booking):
    return booking_data.store_in_db(db_client)

# get booking
@app.get("/booking/{booking_uuid}")
async def get_booking_data(booking_uuid):
    booking_data = db_client.query(f"SELECT get_booking_data('{booking_uuid}')")
    return booking_data[0][0]

# update booking
@app.patch("/update_booking/{booking_uuid}")
async def patch_booking(booking_uuid, booking_patch: BookingPatch):
    target_booking = Booking.instantitate_booking_from_db(booking_uuid, db_client)
    return target_booking.update_in_db(db_client, booking_patch.dict(exclude_unset=True))


# delete booking
@app.delete("/delete_booking/{booking_uuid}")
async def delete_booking(booking_uuid):
    target_booking = Booking.instantitate_booking_from_db(booking_uuid, db_client)
    return target_booking.delete_from_db(db_client)

