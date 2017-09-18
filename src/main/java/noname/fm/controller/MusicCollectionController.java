package noname.fm.controller;

import java.util.List;

import noname.fm.utils.MusicCollection;
import noname.fm.utils.TrackInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dfeodot on 9/18/2017.
 */
@Controller
public class MusicCollectionController {

    @Autowired
    MusicCollection collection;

    @RequestMapping("/getMusicCollection")
    @ResponseBody
    List<TrackInfo> getMusicCollection(){
        return collection.getCollection();
    }
}
