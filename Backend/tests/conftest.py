import os
import pytest
import dotenv

from uuid import uuid4

dotenv.load_dotenv('.env')

def pytest_configure():

    pytest.user_uuid = None

    pytest.location_uuid = None

    pytest.booking_uuid = None

    pytest.address_city = None

    pytest.user = {
            "first_name":           "adam",
            "last_name":            "hanusek",
            "berear":               "abcd",
            "email":                "hanusek.adam@gmail.com",
            "country_phone_code":   "420",
            "phone_number":         "606 556 984",
            "date_of_birth":        "2005-03-09",
            "citizenship":          "czech",
            "id_token":             "dcba"
    }

    pytest.location = {
          "user_uuid": "def49341-aab6-47b7-ab74-cfd5dbe7fdfa",
          "name": "house",
          "beds": 4,
          "description": "nice house in centre of wilderness",
          "address_city": "petrovice u karviné",
          "address_street": "prstná 128",
          "address_country": "czech republic",
          "image1": "images/image1",
}


    pytest.booking = {
          "location_uuid": "c0b48ecb-94a2-4170-b858-7640c7f8d624",
          "booked_user_uuid": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
          "booked_from": "2023-08-20",
          "booked_till": "2022-08-21"
}