import logging
import pytest

from fastapi.testclient import TestClient
from fastapi import status

from main import app

client = TestClient(app=app)

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

class TestCrudOperations():

    # create user
    def test_register_user(self):
        create_user_response = client.post("/register", json=pytest.user)
        pytest.user_uuid = create_user_response.json()["user_uuid"]
        assert create_user_response.status_code == status.HTTP_200_OK
        assert type(create_user_response.json()["user_uuid"]) is str

    # get user data
    def test_user_selection(self):
        select_response = client.get(f"/user/{pytest.user_uuid}")
        assert select_response.status_code == status.HTTP_200_OK
        assert select_response.json()["user_uuid"] == pytest.user_uuid

    # edit user data
    def test_edit_user(self):
        edit_response = client.patch(f"/update_user/{pytest.user_uuid}", json= {
        "first_name":           "Dolfíček",
        "last_name":            "Hitlerů",
        "country_phone_code":   "69"
    })
        assert edit_response.status_code == status.HTTP_200_OK
        assert type(edit_response.json()["user_uuid"]) is str
        select_response = client.get(f'/user/{pytest.user_uuid}')
        assert select_response.json()['user_uuid'] == pytest.user_uuid
        assert select_response.json()["first_name"] == "Dolfíček"

    # # set login data
    # def test_set_login(self):
    #     response = client.post("/login_data", json={
    #     "user_uuid":            pytest.user_uuid,
    #     "email":                "smrt_zidum@gmail.com",
    #     "username":             "dolfík puclík"})
    #     assert response.status_code == status.HTTP_200_OK
    #     assert response.json()["salt"] is not None
    #     select_response = client.get(f'/user/{pytest.user_uuid}')
    #     assert select_response.json()["is_active"] == False
    #
    #
    # # update login
    # def test_update_login(self):
    #     response = client.patch(f"/update_login/{pytest.user_uuid}", json={"hashed_psw": "negr"})
    #     assert response.status_code == status.HTTP_200_OK
    #     assert type(response.json()["user_uuid"]) is str
    #     select_response = client.get(f'/user/{pytest.user_uuid}')
    #     assert select_response.json()["is_active"] == True
    #
    # # delete user login data
    # def test_delete_login(self):
    #     delete_response = client.delete(f"/delete_login/{pytest.user_uuid}")
    #     assert delete_response.status_code == status.HTTP_200_OK

    # login
    def test_user_login(self):
        login_response = client.post("/login", headers={"Authorization": "negr"})
        assert login_response.status_code == status.HTTP_200_OK
        assert login_response.json()["user_uuid"] == pytest.user_uuid

    # delete user data
    def test_delete_user(self):
        delete_response = client.delete(f"/delete_user/{pytest.user_uuid}")
        assert delete_response.status_code == status.HTTP_200_OK


