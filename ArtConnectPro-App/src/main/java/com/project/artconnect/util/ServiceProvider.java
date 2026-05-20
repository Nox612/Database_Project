package com.project.artconnect.util;

import com.project.artconnect.persistence.*;
import com.project.artconnect.service.*;
import com.project.artconnect.service.impl.*;

public class ServiceProvider {

    private static final ArtistService artistService = new ArtistServiceImpl(new JdbcArtistDao());
    private static final ArtworkService artworkService = new ArtworkServiceImpl(new JdbcArtworkDao());
    private static final GalleryService galleryService = new GalleryServiceImpl(new JdbcGalleryDao());
    private static final WorkshopService workshopService = new WorkshopServiceImpl(new JdbcWorkshopDao());
    private static final CommunityService communityService = new CommunityServiceImpl(new JdbcCommunityMemberDao());

    // --> ADD THIS LINE: Instantiate the ExhibitionService with the JdbcExhibitionDao
    private static final ExhibitionService exhibitionService = new ExhibitionServiceImpl(new JdbcExhibitionDao());

    public static ArtistService getArtistService() { return artistService; }
    public static ArtworkService getArtworkService() { return artworkService; }
    public static GalleryService getGalleryService() { return galleryService; }
    public static WorkshopService getWorkshopService() { return workshopService; }
    public static CommunityService getCommunityService() { return communityService; }

    // --> ADD THIS METHOD
    public static ExhibitionService getExhibitionService() { return exhibitionService; }
}