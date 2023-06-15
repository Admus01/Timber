CREATE OR REPLACE FUNCTION select_location_login_by_uuid(_location_uuid UUID)
RETURNS TABLE (
    user_uuid UUID,
    location_uuid UUID,
    name TEXT,
    beds INT,
    description TEXT,
    address_city TEXT,
    address_street TEXT,
    address_apartment_number TEXT,
    address_state TEXT,
    address_country TEXT,
    created_on TIMESTAMPTZ,
    modified_on TIMESTAMPTZ
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    public.locations.user_uuid,
    public.locations.location_uuid,
    public.locations.name,
    public.locations.beds,
    public.locations.description,
    public.locations.address_city,
    public.locations.address_street,
    public.locations.address_apartment_number,
    public.locations.address_state,
    public.locations.address_country,
    login_data.created_on,
    login_data.modified_on
FROM login_data
WHERE login_data.user_uuid = _location_uuid;
END
$$;