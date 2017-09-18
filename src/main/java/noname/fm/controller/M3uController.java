package noname.fm.controller;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class M3uController {

  private static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmm");

  @Autowired
  private RequestParser parser;


  @RequestMapping(value = "/{type:mp3|download}/tilos-{date:.*}.m3u")
  @ResponseBody
  public String generateM3u(HttpServletRequest request, HttpServletResponse response) {
    RequestParser.CollectionWithSize cws = parser.processRequest(request.getRequestURI());


    StringBuilder b = new StringBuilder();
    b.append("#EXTM3U\n");
    b.append(("#EXTINF:" + cws.size + ", Tilos Rádió - " + FILE_NAME_FORMAT.format(cws.collection.getDescriptor().start) + "\n"));
    //workaround for the WP7Application: use the unsplitted version

    b.append("https://archive.tilos.hu" + request.getRequestURI().toString().replaceAll("\\.m3u", ".mp3"));

    String filename = "tilos-" + FILE_NAME_FORMAT.format(cws.collection.getDescriptor().start) + "-" + cws.collection.getDescriptor().duration;
    response.addHeader("Content-Type", "audio/x-mpegurl; charset=utf-8");
    response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".m3u\"");
    response.addHeader("Content-Length", "" + b.toString().getBytes().length);
    return b.toString();

  }

}
