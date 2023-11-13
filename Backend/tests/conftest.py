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
            "first_name":           "Adolf",
            "last_name":            "Hitler",
            "berear":               "negr",
            "email":                "smrt_zidum@gmail.com",
            "country_phone_code":   "88",
            "phone_number":         "69 1234 5678",
            "date_of_birth":        "1889-04-20",
            "citizenship":          "German",
            "id_token":             "negr"
    }
    #
    #
    # pytest.login = {
    #     "user_uuid":            pytest.user_uuid,
    #     "email":                "smrt_zidum@gmail.com",
    #     "username":             "dolfík puclík"
    # }

    pytest.location = {
          "user_uuid": "836c8ae9-4732-48c7-8d40-312482e49242",
          "name": "House",
          "beds": 4,
          "description": "Nice house in centre of wilderness",
          "address_city": "Petrovice u Karviné",
          "address_street": "Prstná 128",
          "address_country": "Czech Republic",
          "image1": "Images/image1",
}


    pytest.booking = {
          "location_uuid": "c0b48ecb-94a2-4170-b858-7640c7f8d624",
          "booked_user_uuid": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
          "booked_from": "2023-08-20",
          "booked_till": "2022-08-21"
}