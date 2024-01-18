CREATE OR REPLACE FUNCTION get_booking_data_by_user_uuid(users_uuid UUID)
RETURNS SETOF JSON
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT
        json_build_object(
            'location_uuid',                public.bookings.location_uuid,
            'booking_uuid',                 public.bookings.booking_uuid,
            'booked_user_uuid',             public.bookings.booked_user_uuid,
            'booked_from',                  public.bookings.booked_from,
            'booked_till',                  public.bookings.booked_till,
            'created_on',                   public.bookings.created_on,
            'modified_on',                  public.bookings.modified_on
        )
    FROM public.bookings
    WHERE public.bookings.booked_user_uuid = users_uuid;
END;
$$;
