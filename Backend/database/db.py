import psycopg2
import psycopg2.pool
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

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