package noname.fm.utils;

/**
 * Created by dfeodot on 9/21/2017.
 */
import java.io.IOException;
import java.io.InputStream;

/**
 * The ThrottleInputStream provides bandwidth throttling on a specified
 * InputStream. It is implemented as a wrapper on top of another InputStream
 * instance.
 * The throttling works by examining the number of bytes read from the underlying
 * InputStream from the beginning, and sleep()ing for a time interval if
 * the byte-transfer is found exceed the specified tolerable maximum.
 * (Thus, while the read-rate might exceed the maximum for a given short interval,
 * the average tends towards the specified maximum, overall.)
 */
public class ThrottledInputStream extends InputStream {

    private final InputStream rawStream;
    private final long maxBytesPerSec;
    private long startTime = System.currentTimeMillis();

    private long bytesRead = 0;
    private long totalSleepTime = 0;
    private long skippingBytes = 0;

    private static final long SLEEP_DURATION_MS = 50;

    public ThrottledInputStream(InputStream rawStream) {
        this(rawStream, Long.MAX_VALUE);
    }

    public ThrottledInputStream(InputStream rawStream, long maxBytesPerSec) {
        assert maxBytesPerSec > 0 : "Bandwidth " + maxBytesPerSec + " is invalid";
        this.rawStream = rawStream;
        this.maxBytesPerSec = maxBytesPerSec;
    }

    @Override
    public void close() throws IOException {
        rawStream.close();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int read() throws IOException {
        Boolean skipping = skippingBytes > 0;
        throttleOrSkip(1);

        int data = rawStream.read();
        if (data != -1 && !skipping) {
            bytesRead++;
        }
        return data;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int read(byte[] b) throws IOException {
        Boolean skipping = skippingBytes > 0;
        throttleOrSkip(b.length);

        int readLen = rawStream.read(b);
        if (readLen != -1 && !skipping) {
            bytesRead += readLen;
        }
        return readLen;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        Boolean skipping = skippingBytes > 0;
        throttleOrSkip(len);

        int readLen = rawStream.read(b, off, len);
        if (readLen != -1 && !skipping) {
            bytesRead += readLen;
        }
        return readLen;
    }

    @Override
    public long skip(long n) throws IOException {
        bytesRead = 0;
        startTime = System.currentTimeMillis();
        skippingBytes = n;
        return super.skip(n);
    }

    private void throttleOrSkip(int len) throws IOException {
        Boolean skipping = false;
        if (skippingBytes > 0) {
            skippingBytes -= len;
        } else {
            throttle();
        }
    }

    private void throttle() throws IOException {
        if (getBytesPerSec() > maxBytesPerSec) {
            try {
                Thread.sleep(SLEEP_DURATION_MS);
                totalSleepTime += SLEEP_DURATION_MS;
            } catch (InterruptedException e) {
                throw new IOException("Thread aborted", e);
            }
        }
    }

    /**
     * Getter for the number of bytes read from this stream, since creation.
     *
     * @return The number of bytes.
     */
    public long getTotalBytesRead() {
        return bytesRead;
    }

    /**
     * Getter for the read-rate from this stream, since creation.
     * Calculated as bytesRead/elapsedTimeSinceStart.
     *
     * @return Read rate, in bytes/sec.
     */
    public long getBytesPerSec() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsed == 0) {
            return bytesRead;
        } else {
            return bytesRead / elapsed;
        }
    }

    /**
     * Getter the total time spent in sleep.
     *
     * @return Number of milliseconds spent in sleep.
     */
    public long getTotalSleepTime() {
        return totalSleepTime;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return "ThrottledInputStream{" +
                "bytesRead=" + bytesRead +
                ", maxBytesPerSec=" + maxBytesPerSec +
                ", bytesPerSec=" + getBytesPerSec() +
                ", totalSleepTime=" + totalSleepTime +
                '}';
    }
}
