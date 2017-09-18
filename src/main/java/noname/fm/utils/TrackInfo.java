package noname.fm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by dfeodot on 9/18/2017.
 */

//outputs a json that should look like
//{
//  artistname: name
//  trackName: name
//  albumName: name
//}

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackInfo {

    private String artistName;
    private String trackName;
    private String albumName;
    private String pathToFile;

    public TrackInfo(String artistName, String trackName, String albumName) {
        this.artistName = artistName;
        this.trackName = trackName;
        this.albumName = albumName;
    }

    //build a track file from a filemane
    //needs parsing and regex so bleah
    public TrackInfo(String fileName){
        if(fileName.contains( "mp3" )) {
            String[] splited = fileName.split( "-" );
            artistName = splited[0];
            albumName = splited[1]+splited[2];
            trackName = splited[3];
            pathToFile = fileName;
        }
    }

    public void setArtistName(String name){
        this.artistName = name;
    }

    public String getArtistName(){
        return artistName;
    }

    public void setTrackName(String name){
        this.trackName = name;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public String getPathToFile() {
        return pathToFile;
    }

}
