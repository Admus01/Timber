CREATE OR REPLACE FUNCTION get_search_data (address_information TEXT, page_number INTEGER, max_page_values INTEGER)
RETURNS JSON
LANGUAGE SQL
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
                'image1', subquery.image1,
                'rating', subquery.rating,
                'number_of_ratings', subquery.number_of_ratings,
                'is_active', subquery.is_active,
                'created_on', subquery.created_on,
                'modified_on', subquery.modified_on))
    FROM (
        SELECT public.locations.location_uuid,
               public.locations.user_uuid,
               public.locations.name,
               public.locations.beds,
               public.locations.description,
               public.locations.address_city,
               public.locations.address_street,
               public.locations.address_apartment_number,
               public.locations.address_state,
               public.locations.address_country,
               public.locations.image1,
               public.locations.rating,
               public.locations.number_of_ratings,
               public.locations.is_active,
               public.locations.created_on,
               public.locations.modified_on
        FROM public.locations
        WHERE (public.locations.address_country = address_information OR public.locations.address_state = address_information
           OR public.locations.address_city = address_information) AND public.locations.is_active = true
        ORDER BY public.locations.location_uuid
        LIMIT max_page_values OFFSET (page_number - 1) * max_page_values
    ) AS subquery;
$$