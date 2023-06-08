CREATE TABLE IF NOT EXISTS login_data (
  login_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_uuid UUID REFERENCES public.users(user_uuid) UNIQUE NOT NULL ,
  email TEXT REFERENCES public.users(email) UNIQUE NOT NULL ,
  username TEXT UNIQUE NOT NULL,
  
);