package com.muratdozen.playground.spotify;

import com.muratdozen.playground.util.io.FastReader;
import com.muratdozen.playground.util.threading.NonThreadSafe;

import java.io.PrintWriter;
import java.util.*;

/**
 * Spotify Challenge, Zipfs Song
 *
 * @author Murat Derya Ozen
 * @since: 9/27/13 9:50 PM
 * @see <a href="https://www.spotify.com/us/jobs/tech/zipfsong/">
 *      https://www.spotify.com/us/jobs/tech/zipfsong/</a>
 */
public class ZipfsSong {

    /**
     * FixedSizePriorityQueue is a priority queue implementation with a fixed size based on a {@link PriorityQueue}.
     * The number of elements in the queue will be at most {@code maxSize}.
     * Once the number of elements in the queue reaches {@code maxSize}, trying to add a new element
     * will remove the lowest element in the queue if the new element is greater than or equal to
     * the current lowest element. The queue will not be modified otherwise.
     */
    @NonThreadSafe
    public static class FixedSizePriorityQueue<E> {
        private final PriorityQueue<E> priorityQueue; /* backing data structure */
        private final Comparator<? super E> comparator;
        private final int maxSize;

        /**
         * Constructs a {@link FixedSizePriorityQueue} with the specified {@code maxSize}
         * and {@code comparator}.
         *
         * @param maxSize    - The maximum size the queue can reach, must be a positive integer.
         * @param comparator - The comparator to be used to compare the elements in the queue, must be non-null.
         */
        public FixedSizePriorityQueue(final int maxSize, final Comparator<? super E> comparator) {
            super();
            if (maxSize <= 0) {
                throw new IllegalArgumentException("maxSize = " + maxSize + "; expected a positive integer.");
            }
            if (comparator == null) {
                throw new NullPointerException("Comparator is null.");
            }
            this.priorityQueue = new PriorityQueue<E>(maxSize, comparator);
            this.comparator = priorityQueue.comparator();
            this.maxSize = maxSize;
        }

        /**
         * Adds an element to the queue. If the queue contains {@code maxSize} elements, {@code e} will
         * be compared to the lowest element in the queue using {@code comparator}.
         * If {@code e} is greater than or equal to the lowest element, that element will be removed and
         * {@code e} will be added instead. Otherwise, the queue will not be modified
         * and {@code e} will not be added.
         *
         * @param e - Element to be added, must be non-null.
         */
        public void add(final E e) {
            if (e == null) {
                throw new NullPointerException("e is null.");
            }
            if (maxSize <= priorityQueue.size()) {
                final E firstElm = priorityQueue.peek();
                if (comparator.compare(e, firstElm) < 1) {
                    return;
                } else {
                    priorityQueue.poll();
                }
            }
            priorityQueue.add(e);
        }

        /**
         * To be not used. Not an efficient operation.
         * @return Returns a sorted view of the queue as a {@link Collections#unmodifiableList(java.util.List)}
         *         unmodifiableList.
         */
        public List<E> asList() {
            return Collections.unmodifiableList(new ArrayList<E>(priorityQueue));
        }
    }

    /**
     * Represents a Song where all fields are public and final.
     */
    public static class Song {

        public static final Comparator<? super Song> COMPARATOR = new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                if (song1.quality == song2.quality) {
                    return -Integer.valueOf(song1.order).compareTo(song2.order);
                }
                return Long.valueOf(song1.quality).compareTo(song2.quality);
            }
        };

        public final long quality;
        public final int order;
        public final String name;

        /**
         * @param quality - Quality of the song must be a non-negative integer.
         * @param order   - Order of the song (as it appears in the album) must be a positive integer.
         * @param name    - Name of the song must be non-null.
         */
        public Song(final long quality, final int order, final String name) {
            super();
            if (quality < 0) {
                throw new IllegalArgumentException("quality = " + quality + "; expected a non-negative integer.");
            }
            if (order <= 0) {
                throw new IllegalArgumentException("order = " + order + "; expected a positive integer.");
            }
            if (name == null) {
                throw new NullPointerException("Name is null.");
            }
            this.quality = quality;
            this.order = order;
            this.name = name;
        }
    }

    private static final void solveUsingFixedPriorityQueue() {
        final FastReader reader = FastReader.from(System.in);
        final int N = reader.nextInt();
        final int M = reader.nextInt();

        final FixedSizePriorityQueue<Song> songsQueue = new FixedSizePriorityQueue<Song>(M,
                Song.COMPARATOR);

        // process each song, add to queue
        for (int i = 1; i <= N; ++i) {
            final long hits = reader.nextLong();
            final long quality = hits * i;
            final String name = reader.next();
            songsQueue.add(new Song(quality, i, name));
        }

        reader.close();

        // output the results in descending order
        final String[] descendingSongNames = new String[M];
        int i = M - 1;
        Song song = null;
        while ((song = songsQueue.priorityQueue.poll()) != null) {
            descendingSongNames[i--] = song.name;
        }

        final PrintWriter writer = new PrintWriter(System.out);
        final String LINE_SEPARATOR = System.getProperty("line.separator");
        for (String str : descendingSongNames) {
            writer.write(str);
            writer.write(LINE_SEPARATOR);
        }

        writer.flush();
        writer.close();

    }

    public static final void main(String[] args) {
        solveUsingFixedPriorityQueue();
    }
}