package noname.fm.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import noname.fm.utils.MusicCollection;
import noname.fm.utils.TrackInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dfeodot on 10/11/2017.
 */
@Controller
public class TrackInfoController {

    @Value("${play.service}")
    private String playService;

    @Value("${img.service}")
    private String imgService;

    @Autowired
    MusicCollection mCollection;

    @RequestMapping(value = "/trackInfo", produces = "application/json")
    @ResponseBody
    public TrackInfoUI getTrackInfo(@RequestParam(value="id") int id){

        TrackInfo track = mCollection.getTrackInfo( id );
        String cover = imgService+"?id="+id;

        String playLink = playService+"?id="+id;
        return new TrackInfoUI(track.getAlbum(), track.getArtist(), playLink, cover, track.getTitle());
    }

    private class TrackInfoUI {

        private String album;
        private String artist;
        //this should be the link to call to play a track
        private String audioTrack;
        private String picture;
        private String title;

        public TrackInfoUI(String album, String artist, String audioTrack, String cover, String title) {
            this.album = album;
            this.artist = artist;
            this.audioTrack = audioTrack;
            this.picture = cover;
            this.title = title;
        }

        public String getAlbum() {
            return album;
        }

        public String getArtist() {
            return artist;
        }

        public String getAudioTrack() {
            return audioTrack;
        }

        public String getPicture() {
            return picture;
        }

        public String getTitle() {
            return title;
        }
    }
}
