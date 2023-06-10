CREATE TABLE IF NOT EXISTS images(
    image_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    image_uuid UUID UNIQUE NOT NULL,
    location_uuid UUID REFERENCES public.locations(location_uuid) UNIQUE NOT NULL,
    image_1 BYTEA NOT NULL,
    image_2 BYTEA,
    image_3 BYTEA,
    image_4 BYTEA,
    image_5 BYTEA,
    image_6 BYTEA,
    image_7 BYTEA,
    image_8 BYTEA,
    image_9 BYTEA,
    image_10 BYTEA,
    image_11 BYTEA,
    image_12 BYTEA,
    image_13 BYTEA,
    image_14 BYTEA,
    image_15 BYTEA,
    image_16 BYTEA,
    image_17 BYTEA,
    image_18 BYTEA,
    image_19 BYTEA,
    image_20 BYTEA,
    created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_on TIMESTAMPTZ NOT NULL DEFAULT NOW()
);