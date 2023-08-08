import psycopg2
import psycopg2.pool
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)
import logging
import os
import dotenv

from fastapi import FastAPI, HTTPException, Response, BackgroundTasks
from database.db import Database
from utils.location import Location, LocationPatch

from utils.user import User, UserPatch
from utils.user_login import UserLogin, UserLoginPatch



dotenv.load_dotenv('.env')

db_config = {
    "database": os.environ.get('database'),
    "host":     os.environ.get('host'),
    "port":     os.environ.get('port'),
    "password": os.environ.get('password'),
    "user":     os.environ.get('user'),
    "sslmode":  os.environ.get('sslmode'),
}
class Database:
    def __init__(self, db_config):
        self.db_config = db_config
        self.connection_pool = psycopg2.pool.SimpleConnectionPool(1, 50, **db_config)

    def _get_connection(self):
        return self.connection_pool.getconn()

    def _release_connection(self, db_connection):
        self.connection_pool.putconn(db_connection)
        return True

    def query(self, statement):
        sql_connection = self._get_connection()
        try:
            cursor = sql_connection.cursor()
            cursor.execute(statement)
            results = cursor.fetchall()
            sql_connection.commit()
            return (results)
        except Exception as E:
            raise (E)
        finally:
            self._release_connection(sql_connection)

    def query_for_image(self, statement):
        sql_connection = self._get_connection()
        try:
            cursor = sql_connection.cursor()
            cursor.execute(statement)
            results = cursor.fetchone()[2]
            sql_connection.commit()
            return results
        except Exception as E:
            raise (E)
        finally:
            self._release_connection(sql_connection)

    def query_with_params(self, statement, params):
        sql_connection = self._get_connection()
        try:
            cursor = sql_connection.cursor()
            cursor.execute(statement, params)
            results = cursor.fetchall()
            sql_connection.commit()
            return (results)
        except Exception as E:
            raise (E)
        finally:
            self._release_connection(sql_connection)

    def query_with_params_headers(self, statement, parameters):
        sql_connection = self._get_connection()
        try:
            cursor = sql_connection.cursor()
            cursor.execute(statement, parameters)
            headers = [description[0] for description in cursor.description]
            results = cursor.fetchall()
            sql_connection.commit()
            return headers, results
        except Exception as E:
            raise E
        finally:
            self._release_connection(sql_connection)

    def execute(self, statement):
        sql_connection = self._get_connection()
        try:
            cursor = sql_connection.cursor()
            cursor.execute(statement)
            sql_connection.commit()
            return True
        except Exception as E:
            raise E
        finally:
            self._release_connection(sql_connection)

    def execute_with_params(self, statement, parameters):
        sql_connection = self._get_connection()
        try:
            cursor = sql_connection.cursor()
            cursor.execute(statement, parameters)
            sql_connection.commit()
            return True
        except Exception as E:
            raise E
        finally:
            self._release_connection(sql_connection)


if __name__ == '__main__':

    conn = psycopg2.connect("host=localhost user=postgres port=5432 dbname=timber")
    cur = conn.cursor()
    cur.execute("UPDATE public.users SET first_name = %s, country_phone_code = %s, phone_number = %s, citizenship = %s, modified_on = %s               WHERE user_uuid = %s;",
                ('negr', '69', 'string', 'somalsko', '2023-07-17 10:25:09.941984', '34dce562-2206-4ac4-a723-c2150322fa1d'))
    conn.commit()
    cur.execute("select * from public.users")
    print(cur.fetchone())
    conn.close()
    cur.close()