import logging
import pytest

from fastapi.testclient import TestClient
from fastapi import status

from main import app
from utils.user import User

client = TestClient(app=app)

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)
log_cli = True
class TestUserClass:
    test_user = User(**pytest.user)

    # def test_user_registration(self):
    #     # response = client.post("/register", json = TestUserClass.test_user.dict())
    #     # assert response.status_code == 200
    #     logger.warning("TestUserClass.test_user")
    #     assert True