CREATE OR REPLACE FUNCTION select_user_by_uuid(_user_uuid UUID)
RETURNS SETOF JSON
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT
        json_build_object(
            'user_uuid' ,public.users.user_uuid,
            'first_name', public.users.first_name,
            'last_name' ,public.users.last_name,
            'email' ,public.users.email,
            'country_phone_code',public.users.country_phone_code,
            'date_of_birth' ,public.users.date_of_birth,
            'citizenship' ,public.users.citizenship,
            'created_on' ,public.users.created_on,
            'modified_on',public.users.modified_on )
    FROM public.users
    WHERE public.users.user_uuid = _user_uuid;
END
$$;