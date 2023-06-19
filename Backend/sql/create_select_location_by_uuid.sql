CREATE OR REPLACE FUNCTION select_location_by_uuid(_location_uuid UUID)
RETURNS SETOF JSON
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT
        json_build_object(
            'user_uuid',                public.locations.user_uuid,
            'location_uuid',            public.locations.location_uuid,
            'name',                     public.locations.name,
            'beds',                     public.locations.beds,
            'description',              public.locations.description,
            'address_city',             public.locations.address_city,
            'address_street',           public.locations.address_street,
            'address_apartment_number', public.locations.address_apartment_number,
            'address_state',            public.locations.address_state,
            'address_country',          public.locations.address_country,
            'created_on',               public.locations.created_on,
            'modified_on',              public.locations.modified_on
        )
    FROM public.locations
    WHERE public.locations.location_uuid = _location_uuid;
END;
$$;
