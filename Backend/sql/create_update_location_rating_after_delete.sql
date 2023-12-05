CREATE OR REPLACE FUNCTION update_location_rating_and_delete_review()
RETURNS TRIGGER AS
$$
DECLARE
    mean_rating FLOAT;
BEGIN

    DELETE FROM reviews
    WHERE review_uuid = OLD.review_uuid;

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


CREATE TRIGGER update_location_rating_after_delete_trigger
AFTER DELETE ON reviews
FOR EACH ROW
EXECUTE FUNCTION update_location_rating_and_delete_review();