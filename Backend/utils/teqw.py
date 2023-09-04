import datetime
import logging
import psycopg2
import io

from fastapi    import HTTPException
from pydantic import BaseModel, Field, validator
from typing     import Optional
from uuid       import uuid4, UUID

logger = logging.getLogger()
logger.setLevel(logging.INFO)

import logging
import os
import uvicorn
import dotenv

from fastapi import FastAPI, HTTPException, Response, BackgroundTasks
from database.db import Database




dotenv.load_dotenv('../.env')

db_config = {
    "database": os.environ.get('database'),
    "host":     os.environ.get('host'),
    "port":     os.environ.get('port'),
    "password": os.environ.get('password'),
    "user":     os.environ.get('user'),
    "sslmode":  os.environ.get('sslmode'),
}

db_client = Database(db_config)



class ImagePatch(BaseModel):
    image_1:                        psycopg2.Binary
    image_2:                        Optional[psycopg2.Binary]
    image_3:                        Optional[psycopg2.Binary]
    image_4:                        Optional[psycopg2.Binary]
    image_5:                        Optional[psycopg2.Binary]
    image_6:                        Optional[psycopg2.Binary]
    image_7:                        Optional[psycopg2.Binary]
    image_8:                        Optional[psycopg2.Binary]
    image_9:                        Optional[psycopg2.Binary]
    image_10:                       Optional[psycopg2.Binary]
    image_11:                       Optional[psycopg2.Binary]
    image_12:                       Optional[psycopg2.Binary]
    image_13:                       Optional[psycopg2.Binary]
    image_14:                       Optional[psycopg2.Binary]
    image_15:                       Optional[psycopg2.Binary]
    image_16:                       Optional[psycopg2.Binary]
    image_17:                       Optional[psycopg2.Binary]
    image_18:                       Optional[psycopg2.Binary]
    image_19:                       Optional[psycopg2.Binary]
    image_20:                       Optional[psycopg2.Binary]
    created_on:                     Optional[str]
    modified_on:                    Optional[str]


class Image(BaseModel):

    image_uuid:                     UUID = Field(default_factory=uuid4) # str = str(uuid4())# str = str(uuid4())
    image:                          psycopg2.Binary | None = None
    created_on:                     Optional[str]
    modified_on:                    Optional[str]

    @validator('image')
    def validate_image(cls, value):
        return value

with open("/Users/adamhanusek/Desktop/Screenshot 2023-08-11 at 21.45.56.png", "rb") as file:
    f = file.read()
    img = psycopg2.Binary(f)

db_client.execute(f"insert into public.images (image) values ({img})")

img_hex = db_client.query_for_image(f"select * from public.images")


image_io = io.BytesIO(img_hex)
with open("../tests/img.png", "wb") as file:
    file.write(img_hex)
