package org.scoreboard.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SortingPoliciesTest {
    private SortingPolicy<Match> sortingPolicy;

    @BeforeEach
    void setUp() {
        sortingPolicy = new SortingByHighestScoreAndMostRecentlyStartedPolicy();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("matchSortingCases")
    void shouldSortMatchesByScoreAndLastUpdated(@SuppressWarnings("unused") String scenario, List<Match> unsortedMatches, List<String> expectedOrder) {
        var sortedMatches = unsortedMatches.stream()
                .sorted(sortingPolicy.apply())
                .toList();

        assertThat(sortedMatches)
                .extracting(Match::getMatchId)
                .containsExactlyElementsOf(expectedOrder);
    }

    static Stream<Arguments> matchSortingCases() {
        return Stream.of(
                Arguments.of(
                        "Different total scores",
                        List.of(
                                createMatch("1", 2, 3, Instant.parse("2024-03-10T10:00:00Z")),
                                createMatch("2", 3, 3, Instant.parse("2024-03-10T09:00:00Z")),
                                createMatch("3", 0, 1, Instant.parse("2024-03-10T12:00:00Z"))
                        ),
                        List.of("2", "1", "3")
                ),
                Arguments.of(
                        "Same total score, different timestamps",
                        List.of(
                                createMatch("1", 2, 3, Instant.parse("2024-03-10T10:00:00Z")),
                                createMatch("2", 1, 4, Instant.parse("2024-03-10T11:00:00Z")),
                                createMatch("3", 3, 3, Instant.parse("2024-03-10T09:00:00Z"))
                        ),
                        List.of("3", "2", "1")
                )
        );
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