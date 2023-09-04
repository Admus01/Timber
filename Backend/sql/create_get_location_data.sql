CREATE OR REPLACE FUNCTION get_location_data (locations_uuid UUID)
returns JSON
language SQL

AS $$
    SELECT json_agg(json_build_object(
           'location_uuid', public.locations.location_uuid,
           'user_uuid', public.locations.user_uuid,
           'name', public.locations.name,
           'beds', public.locations.beds,
           'description', public.locations.description,
           'address_city', public.locations.address_city,
           'address_street', public.locations.address_street,
           'address_apartment_number', public.locations.address_apartment_number,
           'address_state', public.locations.address_state,
           'address_country', public.locations.address_country,
           'is_active', public.locations.is_active,
           'image1', public.locations.image1,
           'image2', public.locations.image2,
           'image3', public.locations.image3,
           'image4', public.locations.image4,
           'image5', public.locations.image5,
           'image6', public.locations.image6,
           'image7', public.locations.image7,
           'image8', public.locations.image8,
           'image9', public.locations.image9,
           'image10', public.locations.image10,
           'created_on', public.locations.created_on,
           'modified_on', public.locations.modified_on))
    FROM public.locations
    WHERE public.locations.location_uuid = locations_uuid;
$$;