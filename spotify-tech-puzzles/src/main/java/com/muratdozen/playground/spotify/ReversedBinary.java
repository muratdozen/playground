package com.muratdozen.playground.spotify;

import com.muratdozen.playground.util.io.FastReader;

/**
 *
 * Spotify Challenge, Reversed Binary
 * @author Murat Derya Ozen
 * @since: 9/27/13 8:47 PM
 * @see <a href="https://www.spotify.com/us/jobs/tech/reversed-binary/">
 *     https://www.spotify.com/us/jobs/tech/reversed-binary/</a>
 *
 */
public class ReversedBinary {

    /**
     * Returns the value obtained by reversing the order of the bits of the specified int value.
     * @param x int must be a positive integer.
     * @return Returns the value obtained by reversing the order of the bits of the specified int value.
     */
    public static final int reverseBits(final int x) {
        assert x >= 1 && x <= 1000000000;
        final int reversed = Integer.reverse(x);
        return reversed >>> Integer.numberOfTrailingZeros(reversed);
    }

    public static final void main(String[] args) {
        final FastReader fastReader = FastReader.from(System.in);
        final int num = fastReader.nextInt();
        System.out.println(reverseBits(num));
    }
}
