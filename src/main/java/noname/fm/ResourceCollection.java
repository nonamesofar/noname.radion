package noname.fm;

import java.util.ArrayList;
import java.util.List;

public class ResourceCollection {

    private Segment descriptor;

    private List<Mp3File> collection = new ArrayList();

    public void add(Mp3File mp3File) {
        collection.add(mp3File);
    }

    public List<Mp3File> getCollection() {
        return collection;
    }

    public void setCollection(List<Mp3File> collection) {
        this.collection = collection;
    }

    public Segment getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Segment descriptor) {
        this.descriptor = descriptor;
    }
}
