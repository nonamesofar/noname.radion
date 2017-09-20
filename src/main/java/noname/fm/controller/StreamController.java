package noname.fm.controller;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import noname.fm.utils.LimitedInputStream;
import noname.fm.utils.MusicCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
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

    private static int DEFAULT_BUFFER_SIZE = 5000000;

    @RequestMapping(value="listen", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    //test with http://localhost:8080/listen?id=7
    public  void getTrack(@RequestParam(value="id") int id, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        ServletOutputStream stream = null;
        BufferedInputStream buf = null;
        try {

            String filePath = mCollection.getCollection().get( id ).getPathToFile();

            stream = response.getOutputStream();

            File file = new File( rootPath + "/" + filePath );

            response.addHeader( "Content-Type", "audio/mpeg" );
            response.addHeader( "Content-Disposition", "inline; filename=\"" + filePath + "\"" );
            response.setContentLength( (int) file.length() );

            FileInputStream input = new FileInputStream( file );
            buf = new BufferedInputStream( input );
            int readBytes = 0;
            //read from the file; write to the ServletOutputStream

            //public synchronized int read(byte b[], int off, int len)
            byte b[] = new byte[DEFAULT_BUFFER_SIZE+1];
            int pos = 0;
            int len = 1000;
            int filelen = (int) file.length();
            do{
                readBytes = buf.read(b, 0, DEFAULT_BUFFER_SIZE);
                if(readBytes!=-1)
                    stream.write(b);
                stream.flush();
                pos += readBytes;
                //len += 1000;

            }while(readBytes!=-1);

//            while ((readBytes = buf.read()) != -1) {
//                stream.write( readBytes );
//            }
            System.out.println("-------------------------------------I DIDS SOMETHING!!!!----------------");
        }catch (SocketTimeoutException e){
            System.out.println( "The socket thing happened! " + e.getMessage() );
        } catch (IOException ioe) {
            //TODO: this throwrs servlet time out - figure out what to do
            System.out.println( "This is just an exception! " + ioe.getMessage() );
            //throw new ServletException( ioe.getMessage() );
        } finally {
            if (stream != null)
                stream.close();
            if (buf != null)
                buf.close();
        }
    }

    @RequestMapping(value = "listen2", produces = "audio/mpeg")
    @ResponseBody
    public Collection<ResourceRegion> streamFile(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {

        String filePath = mCollection.getCollection().get( 5 ).getPathToFile();
        File file = new File( rootPath + "/" + filePath );

        response.addHeader( "Content-Type", "audio/mpeg" );
        response.addHeader( "Content-Disposition", "inline; filename=\"" + filePath + "\"" );
        response.setContentLength( (int) file.length() );

        FileInputStream input = new FileInputStream( file );

        LimitedInputStream lmt = new LimitedInputStream( input, 0, (int) file.length() );
        String range = request.getHeader( HttpHeaders.RANGE);
        List<HttpRange> httpRanges;
        if (range != null) {
            httpRanges = HttpRange.parseRanges(range);

        } else {
            httpRanges = new ArrayList<>();
            httpRanges.add(HttpRange.createByteRange(0));
        }
        return HttpRange.toResourceRegions(httpRanges, new InputStreamResource(lmt) {
            @Override
            public long contentLength() throws IOException {
                return 1;
            }
        });
    }
}
