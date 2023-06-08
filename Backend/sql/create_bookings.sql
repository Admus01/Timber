CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    location_uuid UUID REFERENCES public.locations(location_uuid) UNIQUE NOT NULL,
    booked_user_uuid UUID references public.users(user_uuid) UNIQUE NOT NULL,
    booked_from DATE NOT NULL,
    booked_till DATE NOT NULL,
    created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_on TIMESTAMPTZ NOT NULL DEFAULT NOW()
)