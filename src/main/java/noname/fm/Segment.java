package noname.fm;

import java.util.Date;

public class Segment {

    /**
     * Start of the segment to play.
     */
    public Date start;

    /**
     * Duratin of the segment in sec.
     */
    public int duration;

    public Segment(Date start, int duration) {
        this.start = start;
        this.duration = duration;
    }

    public Segment() {

    }
}
