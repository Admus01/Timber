CREATE OR REPLACE FUNCTION update_number_of_ratings_after_delete()
RETURNS TRIGGER AS
$$
BEGIN

    UPDATE locations
    SET number_of_ratings = number_of_ratings - 1
    WHERE location_uuid = NEW.location_uuid;

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER update_number_of_ratings_after_delete_trigger
AFTER DELETE ON reviews
FOR EACH ROW
EXECUTE FUNCTION update_number_of_ratings_after_delete();
