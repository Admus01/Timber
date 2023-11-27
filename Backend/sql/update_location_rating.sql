CREATE OR REPLACE FUNCTION update_location_rating()
RETURNS TRIGGER AS
$$
DECLARE
    mean_rating FLOAT;
BEGIN
    -- Calculate the arithmetic mean of ratings for the current location_uuid
    SELECT AVG(rating) INTO mean_rating
    FROM reviews
    WHERE location_uuid = NEW.location_uuid;

    -- Update the rating in the locations table
    UPDATE locations
    SET rating = mean_rating
    WHERE location_uuid = NEW.location_uuid;

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER update_location_rating_trigger
AFTER INSERT OR UPDATE OR DELETE ON reviews
FOR EACH ROW
EXECUTE FUNCTION update_location_rating();