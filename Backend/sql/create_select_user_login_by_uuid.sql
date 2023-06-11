CREATE OR REPLACE FUNCTION select_user_login_by_uuid(_user_uuid UUID)
RETURNS TABLE (
    user_uuid UUID,
    email TEXT,
    username TEXT,
    salt TEXT,
    hashed_psw TEXT,
    created_on TIMESTAMPTZ,
    modified_on TIMESTAMPTZ
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    login_data.user_uuid,
    login_data.email,
    login_data.username,
    login_data.salt,
    login_data.hashed_psw,
    login_data.created_on,
    login_data.modified_on
FROM login_data
WHERE login_data.user_uuid = _user_uuid;
END
$$;