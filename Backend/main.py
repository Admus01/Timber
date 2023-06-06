import logging
import os
import uvicorn
import dotenv

from fastapi import FastAPI, HTTPException, Response, BackgroundTasks
from database.db import Database



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
