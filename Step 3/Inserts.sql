Use ArtGallery_DB;

INSERT INTO CITY (CityID, CityName, State) VALUES
  (1, 'New York',     'NY'),
  (2, 'Los Angeles',  'CA'),
  (3, 'Chicago',      'IL'),
  (4, 'Miami',        'FL'),
  (5, 'Austin',       'TX');

INSERT INTO ARTIST (ArtistID, Name, Email, BirthYear, CityID) VALUES
  (1, 'Elena Vasquez',  'elena.vasquez@mail.com',  1985, 1),   -- NY
  (2, 'Marco Tien',     'marco.tien@mail.com',     1978, 2),   -- LA
  (3, 'Sofia Blanc',    'sofia.blanc@mail.com',    1992, 1),   -- NY (same city as Elena)
  (4, 'James Okafor',   'james.okafor@mail.com',   1969, 3),   -- Chicago (senior artist)
  (5, 'Yuki Tanaka',    'yuki.tanaka@mail.com',    2000, 4),   -- Miami (youngest)
  (6, 'Lena Müller',    'lena.muller@mail.com',    1988, 5);   -- Austin

INSERT INTO MEMBER (MemberID, Name, Email, CityID) VALUES
  (1, 'Alice Monroe',   'alice.monroe@mail.com',   1),   -- NY
  (2, 'Bob Castillo',   'bob.castillo@mail.com',   2),   -- LA
  (3, 'Chloe Park',     'chloe.park@mail.com',     3),   -- Chicago
  (4, 'David Nguyen',   'david.nguyen@mail.com',   1),   -- NY
  (5, 'Emma Torres',    'emma.torres@mail.com',    4),   -- Miami
  (6, 'Finn O\'Brien',  'finn.obrien@mail.com',    5),   -- Austin
  (7, 'Grace Kim',      'grace.kim@mail.com',      2),   -- LA
  (8, 'Hugo Ferreira',  'hugo.ferreira@mail.com',  3);   -- Chicago

INSERT INTO GALLERY (GalleryID, Name, Address, Rating, CityID) VALUES
  (1, 'Chelsea Modern',       '245 W 25th St, New York',        5, 1),
  (2, 'Soho Collective',      '110 Greene St, New York',        4, 1),
  (3, 'Pacific Art House',    '850 S Grand Ave, Los Angeles',   5, 2),
  (4, 'Midwest Canvas',       '230 N Michigan Ave, Chicago',    3, 3),
  (5, 'Brickell Gallery',     '1000 Brickell Ave, Miami',       4, 4),
  (6, 'South Congress Arts',  '1600 S Congress Ave, Austin',    4, 5);

INSERT INTO ARTWORK (ArtworkID, Title, Type, Price, Status, ArtistID) VALUES
  -- Elena Vasquez (NY)
  (1,  'Urban Fragments',       'Painting',    4500.00,  'Available',  1),
  (2,  'Concrete Garden',       'Photography', 1200.00,  'Sold',       1),
  (3,  'Steel & Sky',           'Sculpture',   9800.00,  'Available',  1),

  -- Marco Tien (LA)
  (4,  'Pacific Haze',          'Painting',    6200.00,  'Available',  2),
  (5,  'Golden Hour Study',     'Photography', 850.00,   'Available',  2),
  (6,  'Desert Forms I',        'Sculpture',   15000.00, 'Sold',       2),

  -- Sofia Blanc (NY) — emerging artist, lower prices
  (7,  'Whisper Lines',         'Drawing',     600.00,   'Available',  3),
  (8,  'Echo Chamber',          'Mixed Media', 1800.00,  'Reserved',   3),

  -- James Okafor (Chicago) — senior artist, high prices
  (9,  'Heritage No. 7',        'Painting',    22000.00, 'Available',  4),
  (10, 'Ancestral Voices',      'Sculpture',   35000.00, 'Sold',       4),
  (11, 'Diaspora Series III',   'Mixed Media', 18500.00, 'Available',  4),

  -- Yuki Tanaka (Miami) — digital & new media
  (12, 'Neon Tide',             'Digital Art', 3200.00,  'Available',  5),
  (13, 'Coral Memory',          'Photography', 1100.00,  'Available',  5),

  -- Lena Müller (Austin)
  (14, 'Hill Country Dusk',     'Painting',    5400.00,  'Available',  6),
  (15, 'Texas Abstract No. 2',  'Mixed Media', 7200.00,  'Reserved',   6);

INSERT INTO EXHIBITION_INFO (ExhibID, Title, Theme) VALUES
  (1, 'New Horizons',           'Emerging Contemporary Art'),
  (2, 'Roots & Routes',         'Identity and Diaspora'),
  (3, 'Light & Matter',         'Interplay of Photography and Sculpture'),
  (4, 'Digital Frontiers',      'New Media and Technology in Art'),
  (5, 'Solo: James Okafor',     'Retrospective — 30 Years of Work');

INSERT INTO EXHIBITION_SCHEDULE (ExhibID, GalleryID, StartDate) VALUES
  -- "New Horizons" touring show
  (1, 1, '2024-03-01'),   -- Chelsea Modern, NY
  (1, 4, '2024-05-15'),   -- Midwest Canvas, Chicago
  (1, 5, '2024-08-01'),   -- Brickell Gallery, Miami

  -- "Roots & Routes" — two venues
  (2, 2, '2024-04-10'),   -- Soho Collective, NY
  (2, 4, '2024-06-20'),   -- Midwest Canvas, Chicago

  -- "Light & Matter" — concurrent run
  (3, 3, '2024-04-01'),   -- Pacific Art House, LA
  (3, 6, '2024-04-01'),   -- South Congress Arts, Austin  (same date!)

  -- "Digital Frontiers" — Miami-based
  (4, 5, '2024-09-10'),   -- Brickell Gallery, Miami

  -- Solo: James Okafor — flagship gallery
  (5, 1, '2024-11-01');   -- Chelsea Modern, NY

INSERT INTO WORKSHOP_INFO (WS_ID, Title, Price, Level, InstructorID) VALUES
  (1, 'Intro to Oil Painting',          0.00,   'Beginner',     1),   -- Elena Vasquez
  (2, 'Advanced Portrait Techniques',   250.00, 'Advanced',     1),   -- Elena Vasquez (2nd workshop)
  (3, 'Street Photography Essentials',  120.00, 'Beginner',     2),   -- Marco Tien
  (4, 'Sculpting with Found Objects',   180.00, 'Intermediate', 4),   -- James Okafor
  (5, 'Digital Art & NFT Masterclass',  350.00, 'Advanced',     5),   -- Yuki Tanaka
  (6, 'Mixed Media Storytelling',       90.00,  'Intermediate', 6),   -- Lena Müller
  (7, 'Abstract Painting Bootcamp',     200.00, 'Advanced',     3);   -- Sofia Blanc

INSERT INTO WORKSHOP_SCHEDULE (WS_ID, Date) VALUES
  (1, '2024-03-15'),   -- Intro Oil Painting (1st run)
  (1, '2024-06-22'),   -- Intro Oil Painting (2nd run — repeated by demand)
  (2, '2024-04-20'),   -- Advanced Portrait
  (3, '2024-03-15'),   -- Street Photography — same day as WS #1 (parallel!)
  (4, '2024-05-11'),   -- Sculpting
  (5, '2024-09-14'),   -- Digital Art & NFT
  (5, '2024-10-05'),   -- Digital Art & NFT (2nd run)
  (6, '2024-07-27'),   -- Mixed Media
  (7, '2024-08-17');   -- Abstract Bootcamp

INSERT INTO PARTICIPATION (MemberID, WS_ID) VALUES
  -- Intro Oil Painting (free — high attendance)
  (1, 1),   -- Alice
  (2, 1),   -- Bob
  (3, 1),   -- Chloe
  (4, 1),   -- David
  (5, 1),   -- Emma
  (6, 1),   -- Finn

  -- Advanced Portrait
  (1, 2),   -- Alice (also did Intro → progression)
  (4, 2),   -- David (also did Intro → progression)
  (7, 2),   -- Grace (advanced only)

  -- Street Photography
  (2, 3),   -- Bob (LA local attends LA instructor's WS)
  (5, 3),   -- Emma
  (7, 3),   -- Grace (multi-WS: also in Advanced Portrait)
  (8, 3),   -- Hugo

  -- Sculpting with Found Objects
  (3, 4),   -- Chloe (Chicago member, Chicago instructor)
  (4, 4),   -- David (cross-city travel)
  (8, 4),   -- Hugo (also Chicago)

  -- Digital Art & NFT Masterclass
  (2, 5),   -- Bob
  (5, 5),   -- Emma (Miami member, Miami instructor)
  (6, 5),   -- Finn
  (7, 5),   -- Grace (very active: 3rd workshop!)

  -- Mixed Media Storytelling
  (1, 6),   -- Alice (3rd workshop!)
  (6, 6),   -- Finn (Austin member, Austin instructor)
  (3, 6),   -- Chloe

  -- Abstract Painting Bootcamp
  (1, 7),   -- Alice (4th workshop — most active member)
  (2, 7),   -- Bob
  (4, 7);   -- David
