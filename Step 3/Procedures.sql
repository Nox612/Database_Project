Use ArtGallery_DB;
-- PROCEDURES

DELIMITER //

CREATE PROCEDURE CreateEventWithArtist(
    IN p_artist_name VARCHAR(100),
    IN p_genre VARCHAR(50),
    IN p_event_name VARCHAR(100),
    IN p_event_date DATE
)
BEGIN
    DECLARE v_artist_id INT;

    -- Check if artist already exists
    SELECT artist_id
    INTO v_artist_id
    FROM Artist
    WHERE artist_name = p_artist_name
    LIMIT 1;

    -- If artist does not exist, insert new artist
    IF v_artist_id IS NULL THEN
        INSERT INTO Artist (artist_name, genre)
        VALUES (p_artist_name, p_genre);

        SET v_artist_id = LAST_INSERT_ID();
    END IF;

    -- Create the event
    INSERT INTO Events (event_name, event_date, artist_id)
    VALUES (p_event_name, p_event_date, v_artist_id);

END //

DELIMITER ;