import os
import pytest
import dotenv

from uuid import uuid4

dotenv.load_dotenv('.env')

def pytest_configure():

    pytest.user_uuid = None

    pytest.salt = None

    pytest.user = {
        "first_name":           "Adolf",
        "last_name":            "Hitler",
        "email":                "smrt_zidum@gmail.com",
        "country_phone_code":   "88",
        "phone_number":         "69 1234 5678",
        "date_of_birth":        "1889-04-20",
        "citizenship":          "German"
    }
    #
    #
    # pytest.login = {
    #     "user_uuid":            pytest.user_uuid,
    #     "email":                "smrt_zidum@gmail.com",
    #     "username":             "dolfík puclík"
    # }
