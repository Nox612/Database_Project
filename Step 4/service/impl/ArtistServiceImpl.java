package com.project.artconnect.service.impl;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArtistServiceImpl implements ArtistService {
    private final ArtistDao artistDao;

    public ArtistServiceImpl(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistDao.findAll();
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {
        // Since there is no findByName in the DAO interface, we filter the list
        return artistDao.findAll().stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void createArtist(Artist artist) {
        artistDao.save(artist);
    }

    @Override
    public void updateArtist(Artist artist) {
        artistDao.update(artist);
    }

    @Override
    public void deleteArtist(String name) {
        artistDao.delete(name);
    }

    @Override
    public List<Discipline> getAllDisciplines() {
        // Standard disciplines for the filter dropdown
        return List.of(
                new Discipline("Painting"),
                new Discipline("Sculpture"),
                new Discipline("Photography"),
                new Discipline("Digital Art"),
                new Discipline("Music")
        );
    }

    @Override
    public List<Artist> searchArtists(String query, String disciplineName, String city) {
        List<Artist> artists = (city != null && !city.isEmpty()) ? artistDao.findByCity(city) : artistDao.findAll();

        return artists.stream()
                .filter(a -> query == null || a.getName().toLowerCase().contains(query.toLowerCase()))
                // Note: Full discipline filtering requires populating the Artist.disciplines list in the DAO
                .collect(Collectors.toList());
    }
}