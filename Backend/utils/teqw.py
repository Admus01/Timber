import dotenv
import psycopg2
from database.db import Database
import os
import io

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


with open("/Users/adamhanusek/Desktop/Screenshot 2023-06-07 at 9.24.33.png", "rb") as file:
    f = file.read()
    img = psycopg2.Binary(f)

db_client.execute(f"insert into public.test (img) values ({img})")

img_hex = db_client.query_for_image(f"select * from public.test")

image_io = io.BytesIO(img_hex)
with open("../tests/img.png", "wb") as file:
    file.write(img_hex)
