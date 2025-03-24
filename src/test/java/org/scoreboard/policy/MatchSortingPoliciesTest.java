package org.scoreboard.policy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.scoreboard.policy.MatchSortingPolicies.highestScoringMatchesFirst;
import static org.scoreboard.policy.MatchSortingPolicies.mostRecentlyStartedMatchesFirst;

class MatchSortingPoliciesTest {
    @Nested
    class HighestScoringMatchesFirst {
        private final Comparator<Match> policy = highestScoringMatchesFirst();

        @Test
        void shouldSortMatchesByTheScoreSumDesc() {
            var lowerScoredMatch = createMatch("id-1", 3, 5, Instant.now());
            var higherScoredMatch = createMatch("id-1", 5, 4, Instant.now());

            assertThat(Stream.of(lowerScoredMatch, higherScoredMatch)
                    .sorted(policy))
                    .containsExactly(higherScoredMatch, lowerScoredMatch);
        }
    }

    @Nested
    class MostRecentlyStoredMatchesFirst {
        private final Comparator<Match> policy = mostRecentlyStartedMatchesFirst();

        @Test
        void shouldOrderMatchesByMatchStartTimeDesc() {
            var firstStartedMatch = createMatch("id-1", 3, 5, Instant.parse("2024-03-10T10:00:00Z"));
            var secondStartedMatch = createMatch("id-1", 5, 3, Instant.parse("2024-03-10T15:00:00Z"));

            assertThat(Stream.of(firstStartedMatch, secondStartedMatch)
                    .sorted(policy))
                    .containsExactly(secondStartedMatch, firstStartedMatch);
        }
    }

    private static Match createMatch(String matchId, int homeScore, int awayScore, Instant startTime) {
        return new Match(
                matchId,
                new Team("home-id", "name", "displayName"),
                new Team("away-id", "name", "displayName"),
                homeScore,
                awayScore,
                false,
                startTime);
    }
}