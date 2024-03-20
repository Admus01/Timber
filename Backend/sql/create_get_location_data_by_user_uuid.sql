CREATE OR REPLACE FUNCTION get_location_data_by_user_uuid (users_uuid UUID)
returns JSON
language SQL

AS $$
    SELECT json_agg(json_build_object(
           'location_uuid', subquery.location_uuid,
           'user_uuid', subquery.user_uuid,
           'name', subquery.name,
           'beds', subquery.beds,
           'description', subquery.description,
           'address_city', subquery.address_city,
           'address_street', subquery.address_street,
           'address_apartment_number', subquery.address_apartment_number,
           'address_state', subquery.address_state,
           'address_country', subquery.address_country,
           'is_active', subquery.is_active,
           'rating', subquery.rating,
           'image1', subquery.image1,
           'image2', subquery.image2,
           'image3', subquery.image3,
           'image4', subquery.image4,
           'image5', subquery.image5,
           'image6', subquery.image6,
           'image7', subquery.image7,
           'image8', subquery.image8,
           'image9', subquery.image9,
           'image10', subquery.image10,
           'created_on', subquery.created_on))
    FROM (SELECT public.locations.location_uuid,
                 public.locations.user_uuid,
                 public.locations.name,
                 public.locations.beds,
                 public.locations.description,
                 public.locations.address_city,
                 public.locations.address_street,
                 public.locations.address_apartment_number,
                 public.locations.address_state,
                 public.locations.address_country,
                 public.locations.is_active,
                 public.locations.image1,
                 public.locations.image2,
                 public.locations.image3,
                 public.locations.image4,
                 public.locations.image5,
                 public.locations.image6,
                 public.locations.image7,
                 public.locations.image8,
                 public.locations.image9,
                 public.locations.image10,
                 public.locations.rating,
                 public.locations.created_on
          FROM public.locations
          WHERE public.locations.user_uuid = users_uuid
          ORDER BY public.locations.created_on
          LIMIT 20) AS subquery;
$$;