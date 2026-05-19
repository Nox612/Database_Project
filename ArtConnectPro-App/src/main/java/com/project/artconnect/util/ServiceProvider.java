package com.project.artconnect.util;

import com.project.artconnect.persistence.*;
import com.project.artconnect.service.*;
import com.project.artconnect.service.impl.*;

/**
 * Service Provider to manage singleton instances of services.
 * Now configured to use database-backed service implementations.
 */
public class ServiceProvider {

    // Injecting the JDBC DAOs into our concrete Service implementations
    private static final ArtistService artistService = new ArtistServiceImpl(new JdbcArtistDao());
    private static final ArtworkService artworkService = new ArtworkServiceImpl(new JdbcArtworkDao());

    // Note: Assuming you named your other DAOs following the Jdbc... pattern inside the persistence package
    private static final GalleryService galleryService = new GalleryServiceImpl(new JdbcGalleryDao());
    private static final WorkshopService workshopService = new WorkshopServiceImpl(new JdbcWorkshopDao());
    private static final CommunityService communityService = new CommunityServiceImpl(new JdbcCommunityMemberDao());

    public static ArtistService getArtistService() {
        return artistService;
    }

    public static ArtworkService getArtworkService() {
        return artworkService;
    }

    public static GalleryService getGalleryService() {
        return galleryService;
    }

    public static WorkshopService getWorkshopService() {
        return workshopService;
    }

    public static CommunityService getCommunityService() {
        return communityService;
    }
}