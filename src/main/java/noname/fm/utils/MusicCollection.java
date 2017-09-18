package noname.fm.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by dfeodot on 9/18/2017.
 */
@Service
public class MusicCollection {

    @Value("${archive.dir}")
    private String rootPath;

    private final List<TrackInfo> collection = new ArrayList();

    @PostConstruct
    //funny enough, the @Value thing is called after the constructor
    //doing a post contruct to have all the things
    //JAVA is hard
    void buildMusicCollection(){

        //something is triping and I have to new that shit
        //tried injecting the File directly and failed so we read it like human beeings
        File folder = new File( rootPath );

        File[] listOfFiles = folder.listFiles();
        for(File file : listOfFiles){
            if(file.isFile()){
                //temporary hack until I figure out how to DB or read MP3 tags
                //TODO: fix this shit
                collection.add( new TrackInfo( file.getName() ));
            }
        }
    }

    public List<TrackInfo> getCollection(){
        return collection;
    }
}
