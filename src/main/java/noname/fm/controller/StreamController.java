package noname.fm.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import noname.fm.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dfeodot on 9/18/2017.
 */
@Controller
public class StreamController {

    @Value("${archive.dir}")
    private String rootPath;

    @Value("${throttle}")
    private long throttle;

    @Autowired
    MusicCollection mCollection;

    //test with http://localhost:8080/listen?id=7
    @RequestMapping(value="listen", produces = "audio/mpeg")
    public void streamFile(@RequestParam(value="id") int id, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String filePath = mCollection.getCollection().get( id ).getPath();
        File file = new File( rootPath + "/" + filePath );

        MultipartFileSender.fromPath(file.toPath())
                .with(request)
                .with(response)
                .serveResource();
        /*File file = new File( rootPath + "/" + filePath );

        response.addHeader( "Content-Type", "audio/mpeg" );
        response.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.addHeader( "Content-Disposition", "inline; filename=\"" + filePath + "\"" );
        //response.setContentLength( (int) file.length() );

        FileInputStream input = new FileInputStream( file );

        //keeping this as it makes loading a track faster
        //copied from the internet (tilos radio git hub)
        LimitedInputStream lmt = new LimitedInputStream( input, 0, (int) file.length() );
        // throttling the streams somehow gets rid of annoying exceptions and timeouts
        InputStream throttleSteam = new ThrottledInputStream(input, throttle);

        String range = request.getHeader( HttpHeaders.RANGE);
        List<HttpRange> httpRanges;
        if (range != null) {
            httpRanges = HttpRange.parseRanges(range);

        } else {
            httpRanges = new ArrayList<>();
            httpRanges.add(HttpRange.createByteRange(0));
        }
        return HttpRange.toResourceRegions(httpRanges, new InputStreamResource(throttleSteam) {
            @Override
            public long contentLength() throws IOException {
                return (long)file.length();
            }
        });*/
    }
}