CREATE OR REPLACE FUNCTION select_user_login_by_uuid(_user_uuid UUID)
RETURNS SETOF JSON
LANGUAGE plpgsql
AS $$
BEGIN
   RETURN QUERY SELECT
       json_build_object(
           'user_uuid' ,login_data.user_uuid,
           'email',login_data.email,
           'username', login_data.username,
           'salt' ,login_data.salt,
           'hashed_psw' ,login_data.hashed_psw,
           'created_on' ,login_data.created_on,
           'modified_on',login_data.modified_on
       )
   FROM login_data
   WHERE login_data.user_uuid = _user_uuid;
END
$$;