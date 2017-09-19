package noname.fm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.datatype.Artwork;

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

    public TrackInfo() {

    }

    public TrackInfo(String artistName, String trackName, String albumName) {
        this.artistName = artistName;
        this.trackName = trackName;
        this.albumName = albumName;
    }

    //    tag.getFirst(FieldKey.ARTIST),
//            tag.getFirst(FieldKey.ALBUM),
//            tag.getFirst( FieldKey.TITLE),
//            tag.getFirst(FieldKey.YEAR),
//            tag.getFirst(FieldKey.TRACK),
//            tag.getFirstArtwork(),
//            file.getPath()
    public TrackInfo(String artistName, String albumName, String trackName, String year,
                     String trackNo, Artwork artwork, String path) {
        this.artistName = artistName;
        this.trackName = trackName;
        this.albumName = albumName;
        this.pathToFile = path;
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
