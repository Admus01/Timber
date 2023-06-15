import logging
import pytest

from fastapi.testclient import TestClient
from fastapi import status

from main import app

client = TestClient(app=app)

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

class TestCrudOperations():
    def test_register_user(self):
        create_user_response = client.post("/register", json=pytest.user)
        pytest.user_uuid = create_user_response.json()["user_uuid"]
        assert create_user_response.status_code == status.HTTP_200_OK
        assert type(create_user_response.json()["user_uuid"]) is str