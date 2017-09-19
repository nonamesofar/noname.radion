package noname.fm.controller;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import noname.fm.utils.MusicCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
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

    @Autowired
    MusicCollection mCollection;

    @RequestMapping(value="listen", produces = "audio/mpeg")
    @ResponseBody
    //test with http://localhost:8080/listen?id=7
    public void getTrack(@RequestParam(value="id") int id, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        ServletOutputStream stream = null;
        BufferedInputStream buf = null;
        try {

            String filePath = mCollection.getCollection().get( id ).getPathToFile();

            stream = response.getOutputStream();
            File file = new File( rootPath + "/" + filePath );

            response.addHeader( "Content-Type", "audio/mpeg" );
            response.addHeader( "Content-Disposition", "inline; filename=\"" + filePath+ "\"" );
            response.setContentLength( (int) file.length() );

            FileInputStream input = new FileInputStream( file );
            buf = new BufferedInputStream( input );
            int readBytes = 0;
            //read from the file; write to the ServletOutputStream
            while ((readBytes = buf.read()) != -1)
                stream.write( readBytes );
        } catch (IOException ioe) {
            throw new ServletException( ioe.getMessage() );
        } finally {
            if (stream != null)
                stream.close();
            if (buf != null)
                buf.close();
        }
    }
}
