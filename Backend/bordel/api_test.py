import requests

url = "http://127.0.0.1:8000/hello/adam"
data = '''{
    "first_name":           "Adam",
    "last_name":            "Hanusek",
    "email":                "hanusek.adam@gmail.com",
    "country_phone_code":   "420",
    "phone_number":         "606556984",
    "date_of_birth":        "2005-03-09",
    "citizenship":          "Czech"
}'''



print(requests.post(url))