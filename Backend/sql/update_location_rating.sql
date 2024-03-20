CREATE OR REPLACE FUNCTION update_location_rating(locations_uuid UUID)
RETURNS BOOLEAN AS
$$
DECLARE
    mean_rating FLOAT;
BEGIN
    -- Calculate the arithmetic mean of ratings for the current location_uuid
    SELECT AVG(rating) INTO mean_rating
    FROM reviews
    WHERE location_uuid = locations_uuid;

    -- Update the rating in the locations table
    UPDATE locations
    SET rating = mean_rating
    WHERE location_uuid = locations_uuid;

    RETURN TRUE;
END;
$$
LANGUAGE plpgsql;

