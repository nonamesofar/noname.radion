package noname.fm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.datatype.Artwork;

/**
 * Created by dfeodot on 9/18/2017.
 */

//var songs = [
//        {
//        "title": "Malibu",
//        "artist": "Anderson .Paak",
//        "cover": "https://i.pinimg.com/736x/0e/da/c3/0edac388bc5036fff199a62a8ef0ab03--bj-the-chicago-kid-best-album-covers.jpg",
//        "num": "0",
//        "darkColor": "#3A4E55",
//        "lightColor": "#F2F2F2"
//        },
//        {
//        "title": "Let's Get Free",
//        "artist": "Dead Prez",
//        "cover": "https://s-media-cache-ak0.pinimg.com/originals/e4/27/70/e4277010df1aa683d71014e3d97cbf98.jpg",
//        "num": "1",
//        "darkColor": "#4B3A40",
//        "lightColor": "#E0D9C6"
//        },
//        {
//        "title": "Drug Dealers",
//        "artist": "Pusha T",
//        "cover": "https://www.fuse.tv/image/5682de90a151c1fa2b00003c/768/512/pusha-t-darkest-before-dawn-album-cover-full-size.jpg",
//        "num": "2",
//        "darkColor": "#231F16",
//        "lightColor": "#FFF8E8"
//        }
//        ];

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackInfo {

//        {
//        "title": "Malibu",
//        "artist": "Anderson .Paak",
//        "cover": "https://i.pinimg.com/736x/0e/da/c3/0edac388bc5036fff199a62a8ef0ab03--bj-the-chicago-kid-best-album-covers.jpg",
//        "num": "0",
//        "darkColor": "#3A4E55",
//        "lightColor": "#F2F2F2"
//        },
    private String title;
    private String artist;
    private String album;
    private String num;
    private String path;
    private String darkColor = "#3A4E55";
    private String lightColor = "#F2F2F2";

    public TrackInfo() {

    }

    public TrackInfo(String artistName, String trackName, String albumName) {
        this.artist = artistName;
        this.title = trackName;
        this.album = albumName;
    }


    public TrackInfo(String artistName, String albumName, String trackName, String year,
                     String trackNo, Artwork artwork, String path) {
        this.title = trackName;
        this.artist = artistName;
        this.album = albumName;
        this.num = trackNo;
        this.path = path;
        this.darkColor = "#3A4E55";
        this.lightColor = "#F2F2F2";
    }


    public void setArtist(String name){
        this.artist = name;
    }

    public String getArtist(){
        return artist;
    }

    public void setTitle(String name){
        this.title = name;
    }

    public String getTitle() {
        return title;
    }

    public void setAlbum(String albumName) {
        this.album = albumName;
    }

    public String getAlbum() {
        return album;
    }

    public void setPath(String path) { this.path = path; }

    public String getPath() {return path; }

}
