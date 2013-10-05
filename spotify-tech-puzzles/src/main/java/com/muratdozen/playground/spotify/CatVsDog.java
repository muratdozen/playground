package com.muratdozen.playground.spotify;

import com.muratdozen.playground.util.io.FastReader;

import java.io.PrintWriter;
import java.util.*;

/**
 * Spotify Challenge, Cat vs. Dog
 *
 * @author Murat Derya Ozen
 * @since: 10/2/13 10:47 AM
 * @see <a href="https://www.spotify.com/us/jobs/tech/catvsdog/">
 *      https://www.spotify.com/us/jobs/tech/catvsdog/</a>
 */
public class CatVsDog {

    public static class Vote {
        public final boolean catLover;
        public final int keep;
        public final int throwout;

        public Vote(final boolean catLover, final int keep, final int throwout) {
            this.catLover = catLover;
            this.keep = keep;
            this.throwout = throwout;
        }

        public boolean isConflicting(Vote vote) {
            return this.catLover != vote.catLover
                    && (this.keep == vote.throwout || this.throwout == vote.keep);
        }
    }

    // some kind of a bipartite graph matching
    private static final int maxMatchings(final Map<Vote,Set<Vote>> conflictingVotes,
                                          final Map<Vote,Set<Vote>> reverseConflictingVotes) {

        // assign each cat lover vote to a conflicting dog lover vote
        // we need to find out the maximum number of such assignments (matchings)
        // we assign each cat lover vote to a conflicting vote by starting from
        // the cat lover vote that has the least number of options (conflicting votes)

        // list of cat lover votes sorted by the number of options each vote has
        final List<Vote> catLoversVotes = new ArrayList<Vote>(conflictingVotes.keySet());
        Collections.sort(catLoversVotes, new Comparator<Vote>() {
            @Override
            public int compare(Vote vote, Vote vote2) {
                final Set<Vote> set1 = conflictingVotes.get(vote);
                final Set<Vote> set2 = conflictingVotes.get(vote2);
                return Integer.valueOf(set1.size()).compareTo(set2.size());
            }
        });

        int result = 0;

        final int len = catLoversVotes.size();
        // assign each cat lover vote
        for (int i = 0; i < len; ++i) {
            final Vote vote = catLoversVotes.get(i);
            // when choosing which conflicting dog lover vote to assign,
            // choose the one that affects the least amount of remaining cat lover voters
            final Set<Vote> choices = conflictingVotes.get(vote);
            if (choices == null || choices.isEmpty())
                continue;
            Vote minAffectingVote = null; /* dog lover vote that is to be assigned */
            int min = Integer.MAX_VALUE;
            for (Vote choice : choices) {
                final Set<Vote> set = reverseConflictingVotes.get(choice);
                if (set == null || set.isEmpty())
                    continue;
                final int numAffecting = set.size();
                if (numAffecting < min) {
                    min = numAffecting;
                    minAffectingVote = choice;
                }
            }
            if (minAffectingVote != null) {
                reverseConflictingVotes.remove(minAffectingVote);
                ++result;
            }
        }

        return result;
    }

    private static final <T> void addToMap(final Map<T,Set<Vote>> map, final T key, final Vote vote) {
        Set<Vote> votes = map.get(key);
        if (votes == null) {
            votes = new HashSet<Vote>();
            votes.add(vote);
            map.put(key, votes);
        } else {
            votes.add(vote);
        }
    }

    private static final int solveTestCase(final FastReader fastReader) {

        // indexes of cat lover votes and dog lover votes
        final Set<Vote> catLovers = new HashSet<Vote>();
        final Map<Integer, Set<Vote>> dogLoversIndexedByKeep = new HashMap<Integer, Set<Vote>>();
        final Map<Integer, Set<Vote>> dogLoversIndexedByThrowout = new HashMap<Integer, Set<Vote>>();

        // parse and collect data from input
        final int c = fastReader.nextInt();
        final int d = fastReader.nextInt();
        final int v = fastReader.nextInt();

        if (v == 1) return 1;

        // fill in catLovers, dogLoversIndexedByKeep and dogLoversIndexedByThrowout
        for (int j = 0; j < v; ++j) {
            final String keepStr = fastReader.next();
            final String throwoutStr = fastReader.next();
            final int keep = Character.getNumericValue(keepStr.charAt(1));
            final int throwout = Character.getNumericValue(throwoutStr.charAt(1));
            final boolean catLover = keepStr.charAt(0) == 'C';
            final Vote vote = new Vote(catLover, keep, throwout);
            if (catLover) {
                catLovers.add(vote);
            } else {
                addToMap(dogLoversIndexedByKeep, keep, vote);
                addToMap(dogLoversIndexedByThrowout, throwout, vote);
            }
        }

        // build a map of conflicting votes
        // conflictingVotes has cat lovers' votes as keys. each key maps to a set of
        // dog lovers' votes that conflict with key.
        // similarly, reverseConflictingVotes contains dogLovers' votes mapped to a set
        // of conflicting votes.
        final Map<Vote, Set<Vote>> conflictingVotes = new HashMap<Vote, Set<Vote>>(catLovers.size());
        final Map<Vote, Set<Vote>> reverseConflictingVotes = new HashMap<Vote, Set<Vote>>();
        for (Vote catLover : catLovers) {
            final Set<Vote> conflicts = new HashSet<Vote>();
            if (dogLoversIndexedByKeep.containsKey(catLover.throwout)) {
                conflicts.addAll(dogLoversIndexedByKeep.get(catLover.throwout));
            }
            if (dogLoversIndexedByThrowout.containsKey(catLover.keep)) {
                conflicts.addAll(dogLoversIndexedByThrowout.get(catLover.keep));
            }
            conflictingVotes.put(catLover, conflicts);
            for (Vote dogLoverVote : conflicts) {
                addToMap(reverseConflictingVotes, dogLoverVote, catLover);
            }
        }

        // match as many conflicting votes as possible
        // and return number of votes - number of matches
        return v - maxMatchings(conflictingVotes, reverseConflictingVotes);
    }

    public static final void main(String[] args) {
        final FastReader fastReader = FastReader.from(System.in);
        final int numTestCases = fastReader.nextInt();

        final PrintWriter writer = new PrintWriter(System.out);
        final String LINE_SEPARATOR = System.getProperty("line.separator");

        for (int i = 0; i < numTestCases; ++i) {
            final int result = solveTestCase(fastReader);
            writer.write(Integer.toString(result));
            writer.write(LINE_SEPARATOR);
        }

        writer.flush();
        writer.close();
    }
}
