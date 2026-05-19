Use ArtGallery_DB;
-- TRIGGERS

CREATE TRIGGER trg_exhibition_date_check
BEFORE INSERT ON EXHIBITION_SCHEDULE
FOR EACH ROW
BEGIN
    IF NEW.StartDate < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'Exhibition start date cannot be in the past.';
    END IF;
END;

CREATE TRIGGER trg_exhibition_no_overlap
BEFORE INSERT ON EXHIBITION_SCHEDULE
FOR EACH ROW
BEGIN
    DECLARE c INT;
    SELECT COUNT(*) INTO c
    FROM   EXHIBITION_SCHEDULE
    WHERE  GalleryID = NEW.GalleryID
    AND    StartDate = NEW.StartDate;
 
    IF c > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'This gallery already has an exhibition scheduled on that date.';
    END IF;
END;

CREATE TRIGGER trg_workshop_capacity_check
BEFORE INSERT ON PARTICIPATION
FOR EACH ROW
BEGIN
    DECLARE current_count INT;
    DECLARE max_seats     INT;
 
    SELECT COUNT(*) INTO current_count
    FROM   PARTICIPATION
    WHERE  WS_ID = NEW.WS_ID;

    SELECT COALESCE(MaxSeats, 20) INTO max_seats
    FROM   WORKSHOP_CAPACITY
    WHERE  WS_ID = NEW.WS_ID;
 
    IF current_count >= max_seats THEN 
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'Workshop is full. Member has been added to the waitlist.';
    END IF;
END;

