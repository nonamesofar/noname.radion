package noname.fm.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
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


    //funny enough, the @Value thing is called after the constructor
    //doing a post contruct to have all the things
    //JAVA is hard

    //this now scan the folder in the propertiest and build the list
    //pretty smart
    @PostConstruct
    public void readMusicCollection() {

        File rootDir = new File(rootPath);

        File[] fileList = rootDir.listFiles();

        for(File file:fileList){
            try {

                MP3File mp3File = (MP3File)AudioFileIO.read(file);
                Tag tag = mp3File.getTag();

                TrackInfo track = new TrackInfo(
                        tag.getFirst(FieldKey.ARTIST),
                        tag.getFirst(FieldKey.ALBUM),
                        tag.getFirst( FieldKey.TITLE),
                        tag.getFirst(FieldKey.YEAR),
                        tag.getFirst(FieldKey.TRACK),
                        tag.getFirstArtwork(),
                        file.getName()
                );

                collection.add( track );


            //TODO: wish I know how to handle errors
            } catch (CannotReadException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
            } catch (InvalidAudioFrameException e) {
                e.printStackTrace();
            }
        }
    }

    public List<TrackInfo> getCollection(){
        return collection;
    }
}
