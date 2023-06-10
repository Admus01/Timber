import datetime
import logging
import psycopg2
import io

from fastapi    import HTTPException
from pydantic   import BaseModel, Field
from typing     import Optional
from uuid       import uuid4, UUID

logger = logging.getLogger()
logger.setLevel(logging.INFO)

class User_login(BaseModel):
    user_uuid:              Optional[UUID]
    email:                  Optional[str]
    username:               Optional[str]
