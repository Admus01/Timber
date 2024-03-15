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

    # validate user
    def test_validate_user(self):
        validate_user_response = client.get("/validate_email/hanusek.adam@gmail.com")
        assert validate_user_response.status_code == status.HTTP_200_OK
        assert validate_user_response

    # get user data
    def test_user_selection(self):
        select_response = client.get(f"/user/{pytest.user_uuid}")
        assert select_response.status_code == status.HTTP_200_OK
        assert select_response.json()["user_uuid"] == pytest.user_uuid

    # edit user data
    def test_edit_user(self):
        edit_response = client.patch(f"/update_user/{pytest.user_uuid}", json= {
        "first_name":           "adamek",
        "last_name":            "hanusků",
        "country_phone_code":   "240"
    })
        assert edit_response.status_code == status.HTTP_200_OK
        assert type(edit_response.json()["user_uuid"]) is str
        select_response = client.get(f'/user/{pytest.user_uuid}')
        assert select_response.json()['user_uuid'] == pytest.user_uuid
        assert select_response.json()["first_name"] == "adamek"

    # login
    def test_user_login(self):
        login_response = client.post("/login", headers={"Authorization": "dcba"})
        assert login_response.status_code == status.HTTP_200_OK
        assert login_response.json()["user_uuid"] == pytest.user_uuid

    # create location
    def test_create_location(self):
        create_location_response = client.post("/create_location", json=pytest.location)
        pytest.location_uuid = create_location_response.json()["location_uuid"]
        assert create_location_response.status_code == status.HTTP_200_OK
        assert type(create_location_response.json()["location_uuid"]) is str

    # get location data
    def test_get_location(self):
        select_response = client.get(f"/location/{pytest.location_uuid}")
        assert select_response.status_code == status.HTTP_200_OK
        assert select_response.json()["location_uuid"] == pytest.location_uuid

    # patch location data
    def test_update_location(self):
        edit_response = client.patch(f"/update_location/{pytest.location_uuid}", json={
            "name": "Big House",
            "beds": 8
        })
        assert edit_response.status_code == status.HTTP_200_OK
        assert type(edit_response.json()["location_uuid"]) is str
        select_response = client.get(f'/location/{pytest.location_uuid}')
        assert select_response.json()['location_uuid'] == pytest.location_uuid
        assert select_response.json()["name"] == "Big House"

    # create booking
    def test_create_booking(self):
        create_booking_response = client.post("/create_booking", json=pytest.booking)
        pytest.booking_uuid = create_booking_response.json()["booking_uuid"]
        assert create_booking_response.status_code == status.HTTP_200_OK
        assert type(create_booking_response.json()["booking_uuid"]) is str

    # get booking
    def test_get_booking(self):
        select_response = client.get(f"/booking/{pytest.booking_uuid}")
        assert select_response.status_code == status.HTTP_200_OK
        assert select_response.json()["booking_uuid"] == pytest.booking_uuid
    # patch booking
    def test_patch_booking(self):
        edit_response = client.patch(f"/update_booking/{pytest.booking_uuid}", json={
            "booked_from": "2023-07-01"
        })
        assert edit_response.status_code == status.HTTP_200_OK
        assert type(edit_response.json()["booking_uuid"]) is str
        select_response = client.get(f'/booking/{pytest.booking_uuid}')
        assert select_response.json()['booking_uuid'] == pytest.booking_uuid
        assert select_response.json()["booked_from"] == "2023-07-01"
        
    def test_search(self):
        response = client.post("/search", json={"address_information":"petrovice u karviné", "page_index":1})
        assert response.json()["Locations"][0]["location_uuid"] == pytest.location_uuid

    # delete booking
    def test_delete_booking(self):
        delete_response = client.delete(f"/delete_booking/{pytest.booking_uuid}")
        assert delete_response.status_code == status.HTTP_200_OK

    def test_delete_user(self):
        delete_response = client.delete(f"/delete_user/{pytest.user_uuid}")
        assert delete_response.status_code == status.HTTP_200_OK
