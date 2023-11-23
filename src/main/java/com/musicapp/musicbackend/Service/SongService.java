package com.musicapp.musicbackend.Service;

import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.model.Song;
import com.musicapp.musicbackend.model.SongDto;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @Cacheable(value = "songsByTrackNumber", key = "#trackNumber")
    public List<Song> getSongsByTrackNumber(int trackNumber) {
        System.out.println("called from db");
        return songRepository.findByTrackNumber(trackNumber);
    }


public SongDto createSong(@Valid SongDto songDto) {
    if (isTrackNumberExists(songDto.getTrackNumber())) {
        throw new IllegalArgumentException("Track number already exists.");
    }

    Song song = mapDtoToEntity(songDto);

    Artist artist = getOrCreateArtist(String.valueOf(songDto.getArtist()));
    song.setArtist(artist);

    song = songRepository.save(song);
    return mapEntityToDto(song);
}
    private Artist getOrCreateArtist(String artistName) {
        List<Artist> existingArtists = artistRepository.findByArtistName(artistName);
        if (!existingArtists.isEmpty()) {
            return existingArtists.get(0);
        } else {
            Artist newArtist = new Artist();
            newArtist.setArtistName(artistName);
            return artistRepository.save(newArtist);
        }
    }

    public List<SongDto> getAllSongs(int pageNumber, int pageSize) {


        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Song> posts = songRepository.findAll(pageable);


        List<Song> song = posts.getContent();

        List<SongDto> content = song.stream().map(this::mapEntityToDto).toList();
        return content;

//        Pageable p =  PageRequest.of(pageSize, pageNumber);
//        Page<Song> songs = songRepository.findAll(p);
//       List<Song> songs = songRepository.findAll();
//        return mapEntitiesToDto(songs);
    }

    public SongDto getSongById(UUID id) {
        Optional<Song> optionalSong = songRepository.findById(id);
        return optionalSong.map(this::mapEntityToDto).orElse(null);
    }
    public SongDto getSongByFilename(String filename) {
        Optional<Song> optionalSong = songRepository.findByFilename(filename);
        return optionalSong.map(this::mapEntityToDto).orElse(null);
    }
    @CacheEvict(value = "songsById", key = "#id")
    public SongDto updateSong(UUID id, @Valid SongDto updatedDto) {
        Song existingSong = songRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Song not found."));


        if (existingSong.getTrackNumber() != updatedDto.getTrackNumber() && isTrackNumberExists(updatedDto.getTrackNumber())) {
            throw new IllegalArgumentException("Track number already exists.");
        }

        Song updatedSong = mapDtoToEntity(updatedDto);
        //updatedSong.setId(id);
        updatedSong = songRepository.save(updatedSong);

        return mapEntityToDto(updatedSong);
    }

    public void deleteSong(UUID id) {
        songRepository.deleteById(id);
    }

    private boolean isTrackNumberExists(int trackNumber) {
        return false;
        //  return songRepository.existsByTrackNumber(trackNumber);
    }

    private SongDto mapEntityToDto(Song song) {
        SongDto songDTO = new SongDto();
        songDTO.setId(song.getId());
        songDTO.setFilename(song.getFilename());
        songDTO.setFavorite(song.isFavorite());
//        songDTO.setArtist(song.getArtist());
        songDTO.setProducer(song.getProducer());
        songDTO.setTrackNumber(song.getTrackNumber());
        songDTO.setDuration(song.getDuration());

        if (song.getArtist() != null) {
            songDTO.setArtist(ArtistDto.from(song.getArtist()));
        }
        return songDTO;
    }

    private List<SongDto> mapEntitiesToDto(List<Song> songs) {
        return songs.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    private Song mapDtoToEntity(SongDto songDto) {
        Song song = new Song();
        //song.setId(songDto.getId());
        song.setFilename(songDto.getFilename());
        song.setFavorite(songDto.isFavorite());
//        song.setArtist(songDto.getArtist());
        song.setProducer(songDto.getProducer());
        song.setTrackNumber(songDto.getTrackNumber());
        song.setDuration(songDto.getDuration());
//        ArtistDto artistDto = songDto.getArtist();
//        if (artistDto != null) {
//            Optional<Artist> artistOptional = artistRepository.findById(UUID.fromString(artistDto.getId()));
//            artistOptional.ifPresent(song::setArtist);
//        }
        return song;
    }
}
