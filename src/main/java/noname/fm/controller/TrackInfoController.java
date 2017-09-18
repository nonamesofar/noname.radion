package noname.fm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import noname.fm.utils.TrackInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//
///**
// * Created by dfeodot on 9/18/2017.
// */
//
@Controller
public class TrackInfoController {

    @RequestMapping(value = "/info", produces = "application/json")
    @ResponseBody
    public TrackInfo getTrackInfo(HttpServletRequest request, HttpServletResponse response){
        return new TrackInfo("Dan", "Song1", "Album1");
    }
}
