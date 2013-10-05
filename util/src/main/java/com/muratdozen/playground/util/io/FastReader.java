package com.muratdozen.playground.util.io;

import com.muratdozen.playground.util.threading.NonThreadSafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.StringTokenizer;

/**
 * FastReader class helps to read input in the form of words
 * from an {@link InputStream}. Good to use as a parser.
 * <p></p>
 * Usage:
 * <pre>
 *    Assuming an input stream with the following lines:
 *      asd xxx
 *      123
 * {@code
 *  final FastReader fastReader = FastReader.from(System.in);
 *     final String s1 = fastReader.next();
 *     final String s2 = fastReader.next();
 *     final int n = fastReader.nextInt();
 *     ...
 * }
 * </pre>
 *
 * @author Murat Derya Ozen
 * @since: 9/28/13 1:50 PM
 */
@NonThreadSafe
public final class FastReader {

    private final BufferedReader bufferedReader;
    /* legacy class preferred over String#split and Scanner for performance */
    private StringTokenizer tokenizer;

    private FastReader(final BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        this.tokenizer = null;
    }

    /**
     * Returns a {@link FastReader} instance that reads input from {@code inputStream}.
     *
     * @param inputStream
     * @return Returns a {@link FastReader} instance that reads input from {@code inputStream}.
     */
    public static final FastReader from(final InputStream inputStream) {
        return new FastReader(new BufferedReader(new InputStreamReader(inputStream)));
    }

    /**
     * Returns the next word acquired by {@link StringTokenizer}.
     * Moves on to the next line if the current line has been processed.
     *
     * @return Returns the next word acquired by {@link StringTokenizer},
     *         or null if end of stream has been reached.
     * @throws RuntimeException If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     */
    public String next() {
        return tokenize() ? tokenizer.nextToken() : null;
    }

    /**
     * Checks to see if there are any more words left in the {@code inputStream}.
     * Can be used to check if end of stream has been reached, as well.
     * If required, reads another line from the {@code inputStream}; i.e this operation
     * might perform an I/O; possibly block if end of stream is not reached but stream
     * is not yet available to yield a new line.
     *
     * @return Returns true if there are more words to read in the {@code inputStream}
     *         and end of stream has not been reached. False otherwise.
     * @throws RuntimeException If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     */
    public boolean canReadMore() {
        return tokenize();
    }

    private boolean tokenize() {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            // read a line, see if end of stream has been reached
            String line = null;
            try {
                if ((line = bufferedReader.readLine()) == null) return false;
            } catch (IOException unexpected) {
                throw new RuntimeException(unexpected);
            }
            tokenizer = new StringTokenizer(line);
        }
        return true;
    }

    /**
     * Returns the next {@code int} acquired by {@link StringTokenizer}
     * using {@link Integer#parseInt(String)} on {@link #next()}.
     * Moves on to the next line if the current line has been processed.
     *
     * @return Returns the next {@code int} acquired by {@link StringTokenizer}.
     * @throws RuntimeException      If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     * @throws NumberFormatException If an invalid input is encountered or end of stream has been reached.
     */
    public int nextInt() {
        return Integer.parseInt(next());
    }

    /**
     * Returns the next {@code long} acquired by {@link StringTokenizer}
     * using {@link Long#parseLong(String)} on {@link #next()}.
     * Moves on to the next line if the current line has been processed.
     *
     * @return Returns the next {@code long} acquired by {@link StringTokenizer}.
     * @throws RuntimeException      If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     * @throws NumberFormatException If an invalid input is encountered or end of stream has been reached.
     */
    public long nextLong() {
        return Long.parseLong(next());
    }

    /**
     * Returns the next {@code double} acquired by {@link StringTokenizer}
     * using {@link Double#parseDouble(String)} on {@link #next()}.
     * Moves on to the next line if the current line has been processed.
     *
     * @return Returns the next {@code double} acquired by {@link StringTokenizer}.
     * @throws RuntimeException      If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     * @throws NumberFormatException If an invalid input is encountered or end of stream has been reached.
     */
    public double nextDouble() {
        return Double.parseDouble(next());
    }

    /**
     * Returns the next {@link BigDecimal} acquired by {@link StringTokenizer}
     * using BigDecimal's String constructor on {@link #next()}.
     * Moves on to the next line if the current line has been processed.
     *
     * @return Returns the next {@code BigDecimal} acquired by {@link StringTokenizer}.
     * @throws RuntimeException      If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     * @throws NumberFormatException If an invalid input is encountered or end of stream has been reached.
     */
    public BigDecimal nextBigDecimal() {
        return new BigDecimal(next());
    }

    /**
     * Returns the next {@link BigInteger} acquired by {@link StringTokenizer}
     * using BigInteger's String constructor on {@link #next()}.
     * Moves on to the next line if the current line has been processed.
     *
     * @return Returns the next {@code BigInteger} acquired by {@link StringTokenizer}.
     * @throws RuntimeException      If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     * @throws NumberFormatException If an invalid input is encountered or end of stream has been reached.
     */
    public BigInteger nextBigInteger() {
        return new BigInteger(next());
    }

    /**
     * Closes the input stream.
     *
     * @throws RuntimeException If {@link java.io.BufferedReader#readLine()} throws an {@link IOException}.
     * @see java.io.BufferedReader#close()
     */
    public void close() {
        try {
            bufferedReader.close();
        } catch (IOException unexpected) {
            throw new RuntimeException(unexpected);
        }
    }
}
