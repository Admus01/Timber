insert into users (user_uuid, first_name, last_name, email, date_of_birth) VALUES ('37ac70d3-9eb0-42f9-a971-07c873413f10', 's', 'f', 'b', '2019-01-01');
insert into login_data (user_uuid, email, username) VALUES ('37ac70d3-9eb0-42f9-a971-07c873413f10', 'b', 'c');
UPDATE login_data SET hashed_psw = crypt('auto', (SELECT salt FROM login_data WHERE email = 'b')) WHERE email = 'b';