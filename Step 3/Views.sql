Use ArtGallery_DB;
-- VIEWS

-- Only public info of artists
CREATE VIEW vw_PublicArtistProfile AS
SELECT 
    a.ArtistID, 
    a.Name AS ArtistName, 
    c.CityName, 
    c.State
FROM 
    ARTIST a
LEFT JOIN 
    CITY c ON a.CityID = c.CityID;
    
-- Only available artworks
CREATE VIEW vw_AvailableArtworks AS
SELECT 
    aw.ArtworkID, 
    aw.Title AS ArtworkTitle, 
    ar.Name AS ArtistName, 
    aw.Type, 
    aw.Price
FROM 
    ARTWORK aw
JOIN 
    ARTIST ar ON aw.ArtistID = ar.ArtistID
WHERE 
    aw.Status = 'Available';
    
-- All exhibitions
CREATE VIEW vw_ExhibitionScheduleDetails AS
SELECT 
    es.StartDate,
    ei.Title AS ExhibitionTitle, 
    ei.Theme, 
    g.Name AS GalleryName, 
    g.Address,
    c.CityName
FROM 
    EXHIBITION_SCHEDULE es
JOIN 
    EXHIBITION_INFO ei ON es.ExhibID = ei.ExhibID
JOIN 
    GALLERY g ON es.GalleryID = g.GalleryID
LEFT JOIN 
    CITY c ON g.CityID = c.CityID;
    
-- Data of workshops
CREATE VIEW vw_WorkshopEnrollmentStats AS
SELECT 
    wi.WS_ID, 
    wi.Title AS WorkshopTitle, 
    a.Name AS InstructorName, 
    wi.Price,
    wi.Level,
    COUNT(p.MemberID) AS TotalParticipants,
    (COUNT(p.MemberID) * wi.Price) AS EstimatedRevenue
FROM 
    WORKSHOP_INFO wi
JOIN 
    ARTIST a ON wi.InstructorID = a.ArtistID
LEFT JOIN 
    PARTICIPATION p ON wi.WS_ID = p.WS_ID
GROUP BY 
    wi.WS_ID, 
    wi.Title, 
    a.Name, 
    wi.Price, 
    wi.Level;

-- No contact info
CREATE VIEW vw_MemberDirectory AS
SELECT 
    m.MemberID, 
    m.Name AS MemberName, 
    c.CityName, 
    c.State
FROM 
    MEMBER m
LEFT JOIN 
    CITY c ON m.CityID = c.CityID;
    
-- Value of artist's art
CREATE VIEW vw_ArtistInventoryValue AS
SELECT 
    a.ArtistID, 
    a.Name AS ArtistName, 
    COUNT(aw.ArtworkID) AS AvailableArtCount, 
    SUM(aw.Price) AS TotalInventoryValue
FROM 
    ARTIST a
JOIN 
    ARTWORK aw ON a.ArtistID = aw.ArtistID
WHERE 
    aw.Status = 'Available'
GROUP BY 
    a.ArtistID, 
    a.Name;
    
-- History of workshops of a member
CREATE VIEW vw_MemberWorkshopHistory AS
SELECT 
    m.Name AS MemberName, 
    m.Email, 
    w.Title AS WorkshopTitle, 
    w.Level, 
    a.Name AS InstructorName
FROM 
    MEMBER m
JOIN 
    PARTICIPATION p ON m.MemberID = p.MemberID
JOIN 
    WORKSHOP_INFO w ON p.WS_ID = w.WS_ID
JOIN 
    ARTIST a ON w.InstructorID = a.ArtistID;

-- Best galleries
CREATE VIEW vw_TopRatedGalleries AS
SELECT 
    g.GalleryID,
    g.Name AS GalleryName, 
    g.Address, 
    c.CityName, 
    c.State, 
    g.Rating
FROM 
    GALLERY g
JOIN 
    CITY_DATA c ON g.CityID = c.CityID
WHERE 
    g.Rating >= 4.0;
    
-- Simple view for members
CREATE VIEW vw_WorkshopScheduleCalendar AS
SELECT 
    ws.Date AS SessionDate, 
    wi.Title AS WorkshopTitle, 
    wi.Level, 
    wi.Price, 
    a.Name AS InstructorName
FROM 
    WORKSHOP_SESSION ws
JOIN 
    WORKSHOP_INFO wi ON ws.WS_ID = wi.WS_ID
JOIN 
    ARTIST a ON wi.InstructorID = a.ArtistID;
    
-- Unavailable artworks
CREATE VIEW vw_SoldArtworksAudit AS
SELECT 
    aw.ArtworkID, 
    aw.Title, 
    aw.Type, 
    aw.Price, 
    aw.Status,
    a.Name AS ArtistName
FROM 
    ARTWORK aw
JOIN 
    ARTIST a ON aw.ArtistID = a.ArtistID
WHERE 
    aw.Status != 'Available';