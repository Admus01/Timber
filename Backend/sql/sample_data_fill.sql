insert into users (user_uuid, first_name, last_name, email, date_of_birth) VALUES ('37ac70d3-9eb0-42f9-a971-07c873413f10', 's', 'f', 'b', '2019-01-01');
insert into login_data (user_uuid, email, username) VALUES ('37ac70d3-9eb0-42f9-a971-07c873413f10', 'b', 'c');
insert into locations (
                       location_uuid,
                       user_uuid,
                       name,
                       beds,
                       description,
                       address_city,
                       address_street,
                       address_apartment_number,
                       address_state,
                       address_country
                       )
        VALUES (
                '6e643a4d-371d-4f8a-afb3-e5361b9c7cfe',
                '37ac70d3-9eb0-42f9-a971-07c873413f10',
                'Chata',
                 88,
                'chata cype co vic chces',
                'shrekova bazina',
                'adolfa hitlera',
                 33,
                 'osvětim',
                 'Německo'
                );
UPDATE login_data SET hashed_psw = crypt('auto', (SELECT salt FROM login_data WHERE email = 'b')) WHERE email = 'b';

drop table images