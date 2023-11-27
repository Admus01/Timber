CREATE OR REPLACE FUNCTION get_reviews_by_best (location_uuid UUID, page_number INTEGER, max_page_values INTEGER)
RETURNS JSON
LANGUAGE SQL
AS $$
    SELECT json_agg(json_build_object(
                'user_uuid', subquery.user_uuid,
                'title', subquery.title,
                'review', subquery.review,
                'rating', subquery.rating,
                'created_on', subquery.created_on,
                'modified_on', subquery.modified_on))
    FROM (
        SELECT public.reviews.user_uuid,
               public.reviews.title,
               public.reviews.review,
               public.reviews.rating,
               public.reviews.created_on,
               public.reviews.modified_on
        FROM public.reviews
        WHERE public.reviews.location_uuid = location_uuid
        ORDER BY public.reviews.rating
        LIMIT max_page_values OFFSET (page_number - 1) * max_page_values
    ) AS subquery;
$$