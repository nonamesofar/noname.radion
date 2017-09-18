package noname.fm.controller;

import noname.fm.CombinedInputStream;
import noname.fm.LimitedInputStream;
import noname.fm.Mp3File;
import noname.fm.ThrottledInputStream;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StreamerController {

  private static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmm");

  @Value("${archive.dir}")
  private File root;

  @Value("${cache.dir}")
  private File cacheDir;

  private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

  @Value("${throttle}")
  private long throttle;

  @Autowired
  private RequestParser parser;


  //need cors to access this from another app/the UI
  @CrossOrigin(origins = "http://localhost:8080")
  @RequestMapping(value = "/listen", produces = "audio/mpeg")
  @ResponseBody
  public Collection<ResourceRegion> streamFile( HttpServletRequest request, HttpServletResponse response) {

    String uri = request.getRequestURI();
    uri = uri.substring(uri.lastIndexOf("/") + 1);

    File cachefile = new File(cacheDir, uri);
    if (cachefile.exists() && cachefile.length() > 0) {
      try {
        response.sendRedirect("https://archive.tilos.hu/cache/" + uri);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return null;
    }


    MDC.put("requestId", "" + Math.round(Math.random() * 10000));

    RequestParser.CollectionWithSize cws = parser.processRequest(request.getRequestURI());

    response.addHeader("Content-Type", "audio/mpeg");
    response.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

    //response.addHeader(  "Access-Control-Allow-Origin", "http://localhost:8090/listen");

    String filename = "tilos-" + FILE_NAME_FORMAT.format(cws.collection.getDescriptor().start) + "-" + cws.collection.getDescriptor().duration;

    if (request.getRequestURI().contains("download")) {
      response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".mp3\"");
    } else {
      response.addHeader("Content-Disposition", "inline; filename=\"" + filename + ".mp3\"");
    }

    InputStream[] inputStreams = cws.collection.getCollection().stream().map(mp3File -> {
      try {
        FileInputStream fileInputStream = new FileInputStream(new File(root, mp3File.getName()));
        return new LimitedInputStream(fileInputStream, mp3File.getStartOffset(), mp3File.getEndOffset());
      } catch (Exception e) {
        throw new RuntimeException("Can't stream the files", e);
      }
    }).collect(Collectors.toList()).toArray(new InputStream[0]);
    InputStream combinedInputStream = new ThrottledInputStream(new CombinedInputStream(inputStreams), throttle);

    Mp3File mp3File = cws.collection.getCollection().get(0);

    Resource result = new InputStreamResource(combinedInputStream);
    String range = request.getHeader(HttpHeaders.RANGE);
    List<HttpRange> httpRanges;
    if (range != null) {
      httpRanges = HttpRange.parseRanges(range);

    } else {
      httpRanges = new ArrayList<>();
      httpRanges.add(HttpRange.createByteRange(0));
    }
    return HttpRange.toResourceRegions(httpRanges, new InputStreamResource(combinedInputStream) {
      @Override
      public long contentLength() throws IOException {
        return cws.size;
      }
    });

  }


}
