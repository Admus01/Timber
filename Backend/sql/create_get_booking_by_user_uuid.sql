CREATE OR REPLACE FUNCTION get_booking_data_by_user_uuid(users_uuid UUID)
RETURNS SETOF JSON
LANGUAGE SQL
AS $$
    SELECT
        json_agg(json_build_object(
            'location_uuid', subquery.location_uuid,
            'booking_uuid', subquery.booking_uuid,
            'booked_user_uuid', subquery.booked_user_uuid,
            'booked_from', subquery.booked_from,
            'booked_till', subquery.booked_till,
            'created_on', subquery.created_on,
            'modified_on', subquery.modified_on
        ))
    FROM (
        SELECT public.bookings.location_uuid,
                public.bookings.booking_uuid,
                public.bookings.booked_user_uuid,
                public.bookings.booked_from,
                public.bookings.booked_till,
                public.bookings.created_on,
                public.bookings.modified_on
        FROM public.bookings
        WHERE public.bookings.booked_user_uuid = users_uuid
        ORDER BY public.bookings.booked_from
        LIMIT 20
        ) AS subquery;
$$;
