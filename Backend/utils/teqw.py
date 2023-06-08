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

class AppliancePatch(BaseModel):
    appliance_uuid:                 str
    company_uuid:                   Optional[str]
    location_uuid:                  Optional[str]
    vendor_uuid:                    Optional[str]
    key_uuid:                       Optional[str]
    serial_number:                  Optional[str]
    hostname:                       Optional[str]
    appliance_parent_uuid:          Optional[str]
    appliance_description:          Optional[str]
    contract_site_customer_name:    Optional[str]
    contract_site_address1:         Optional[str]
    contract_site_city:             Optional[str]
    contract_site_state_province:   Optional[str]
    contract_site_country:          Optional[str]
    covered_product_line_end_date:  Optional[str]
    is_covered:                     Optional[str]
    orderable_pid:                  Optional[str]
    pillar_code:                    Optional[str]
    service_contract_number:        Optional[str]
    service_line_descr:             Optional[str]
    warranty_end_date:              Optional[str]
    warranty_type:                  Optional[str]
    warranty_type_description:      Optional[str]
    vendor_notes:                   Optional[str]
    created_on:                     Optional[str]
    modified_on:                    Optional[str]

class Appliance(BaseModel):
    company_uuid:                   Optional[str]
    location_uuid:                  Optional[str]
    vendor_uuid:                    Optional[str]
    key_uuid:                       Optional[str]
    appliance_parent_uuid:          Optional[str]
    appliance_uuid:                 UUID = Field(default_factory=uuid4) # str = str(uuid4())# str = str(uuid4())
    serial_number:                  str
    hostname:                       Optional[str]
    appliance_description:          Optional[str]
    contract_site_customer_name:    Optional[str]
    contract_site_address1:         Optional[str]
    contract_site_city:             Optional[str]
    contract_site_state_province:   Optional[str]
    contract_site_country:          Optional[str]
    covered_product_line_end_date:  Optional[str]
    is_covered:                     Optional[str]
    orderable_pid:                  Optional[str]
    pillar_code:                    Optional[str]
    service_contract_number:        Optional[str]
    service_line_descr:             Optional[str]
    warranty_end_date:              Optional[str]
    warranty_type:                  Optional[str]
    warranty_type_description:      Optional[str]
    vendor_notes:                   Optional[str]
    created_on:                     Optional[str]
    modified_on:                    Optional[str]


with open("/Users/adamhanusek/Desktop/Screenshot 2023-06-07 at 9.24.33.png", "rb") as file:
    f = file.read()
    img = psycopg2.Binary(f)

db_client.execute(f"insert into public.test (img) values ({img})")

img_hex = db_client.query_for_image(f"select * from public.test")

image_io = io.BytesIO(img_hex)
with open("../tests/img.png", "wb") as file:
    file.write(img_hex)
