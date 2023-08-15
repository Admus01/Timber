CREATE OR REPLACE FUNCTION get_user_data (users_uuid UUID)
returns JSON
language SQL

AS $$
    SELECT json_agg(json_build_object(
           'user_uuid', public.users.user_uuid,
           'first_name', public.users.first_name,
           'last_name', public.users.last_name,
           'email', public.users.email,
           'country_phone_code', public.users.country_phone_code,
           'phone_number', public.users.phone_number,
           'date_of_birth', public.users.date_of_birth,
           'citizenship', public.users.citizenship,
           'is_master_admin', public.users.is_master_admin,
           'is_active', public.users.is_active,
           'is_deleted', public.users.is_deleted,
           'created_on', public.users.created_on,
           'modified_on', public.users.modified_on))
    FROM public.users
    WHERE public.users.user_uuid = users_uuid;
$$;