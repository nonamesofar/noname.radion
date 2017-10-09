package noname.fm.controller;

import java.io.IOException;
import java.io.InputStream;

import noname.fm.utils.MusicCollection;
import org.jaudiotagger.tag.datatype.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dfeodot on 10/9/2017.
 */
@Controller
public class ArtworkController {

    @Autowired
    MusicCollection mCollection;

    @ResponseBody
    @RequestMapping(value = "/cover", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)

    public byte[] getAlbumArtwork(@RequestParam(value="id") int id) throws IOException {

        byte[] art = mCollection.getArtAsBytes(id);
        return art;
    }
}
