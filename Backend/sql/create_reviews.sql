CREATE TABLE IF NOT EXISTS reviews(
    review_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    review_uuid UUID NOT NULL,
    location_uuid UUID REFERENCES public.locations(location_uuid) NOT NULL,
    user_uuid UUID REFERENCES public.users(user_uuid) NOT NULL,
    title TEXT NOT NULL,
    review TEXT NOT NULL,
    rating FLOAT NOT NULL,
    created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_on TIMESTAMPTZ NOT NULL DEFAULT NOW()
)