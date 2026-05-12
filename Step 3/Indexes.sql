Use ArtGallery_DB;

CREATE INDEX idx_artwork_status 
ON ARTWORK (Status);

CREATE INDEX idx_artist_name 
ON ARTIST (Name);

CREATE INDEX idx_member_name 
ON MEMBER (Name);
