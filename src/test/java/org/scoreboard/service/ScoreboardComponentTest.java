package org.scoreboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scoreboard.exception.MatchNotFoundException;
import org.scoreboard.exception.OngoingMatchException;
import org.scoreboard.exception.ValidationException;
import org.scoreboard.model.Team;
import org.scoreboard.policy.SortingByHighestScoreAndMostRecentlyStartedPolicy;
import org.scoreboard.repository.InMemoryMatchRepository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoreboardComponentTest {
    private final Scoreboard scoreboard = createScoreboard();

    @Test
    @DisplayName("Start match - should create and store match")
    void shouldStartMatch() {
        var now = Instant.now();
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);

        assertThat(match).isNotNull();
        assertThat(match.getHomeTeam()).isSameAs(homeTeam);
        assertThat(match.getAwayTeam()).isSameAs(awayTeam);
        assertThat(match.getHomeScore()).isEqualTo(0);
        assertThat(match.getAwayScore()).isEqualTo(0);
        assertThat(match.isFinished()).isFalse();
        assertThat(match.getStartTime()).isAfterOrEqualTo(now);
    }

    @Test
    @DisplayName("validateTeams - should throw ValidationException for null teams")
    void shouldThrowExceptionWhenTeamsAreNull() {
        assertThatThrownBy(() -> scoreboard.startMatch(null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("validateTeams - should throw ValidationException for same teams")
    void shouldThrowExceptionWhenTeamsAreSame() {
        var team = createTeam("1", "SameTeam");

        assertThatThrownBy(() -> scoreboard.startMatch(team, team))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("validateTeams - should throw OngoingMatchException when team has already at least one ongoing match in scoreboard")
    void shouldThrowExceptionWhenTeamsHaveAtLeastOneOngoingMatchInScoreboard() {
        var homeTeam = createTeam("1", "HomeTeam");
        var homeTeam2 = createTeam("2", "HomeTeam2");
        var awayTeam = createTeam("3", "AwayTeam");

        scoreboard.startMatch(homeTeam, awayTeam);

        assertThatThrownBy(() -> scoreboard.startMatch(homeTeam2, awayTeam))
                .isInstanceOf(OngoingMatchException.class)
                .hasMessageContaining("3");
    }

    @Test
    @DisplayName("Update score - should modify existing match")
    void shouldUpdateScore() {
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);
        var matchId = match.getMatchId();
        var timestamp = match.getStartTime();

        var updatedMatch = scoreboard.updateScore(matchId, 2, 1);

        assertThat(updatedMatch).isNotNull();
        assertThat(updatedMatch.getMatchId()).isEqualTo(matchId);
        assertThat(updatedMatch.getHomeTeam()).isSameAs(homeTeam);
        assertThat(updatedMatch.getAwayTeam()).isSameAs(awayTeam);
        assertThat(updatedMatch.getHomeScore()).isEqualTo(2);
        assertThat(updatedMatch.getAwayScore()).isEqualTo(1);
        assertThat(updatedMatch.isFinished()).isFalse();
        assertThat(updatedMatch.getStartTime()).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("validateScore - should throw ValidationException for negative scores")
    void shouldThrowExceptionWhenScoresAreNegative() {
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);
        var matchId = match.getMatchId();

        assertThatThrownBy(() -> scoreboard.updateScore(matchId, -1, 0))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("updateScore - should throw MatchNotFoundException when match does not exist")
    void shouldThrowExceptionWhenUpdatingNonExistentMatch() {
        var nonExistentMatchId = "invalid-id";

        assertThatThrownBy(() -> scoreboard.updateScore(nonExistentMatchId, 2, 3))
                .isInstanceOf(MatchNotFoundException.class)
                .hasMessageContaining(nonExistentMatchId);
    }

    @Test
    @DisplayName("Finish match - should mark match as finished")
    void shouldFinishMatch() {
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);
        var matchId = match.getMatchId();
        var timestamp = match.getStartTime();

        var finishedMatch = scoreboard.finishMatch(matchId);

        assertThat(finishedMatch).isNotNull();
        assertThat(finishedMatch.getMatchId()).isEqualTo(matchId);
        assertThat(finishedMatch.getHomeTeam()).isSameAs(homeTeam);
        assertThat(finishedMatch.getAwayTeam()).isSameAs(awayTeam);
        assertThat(finishedMatch.getHomeScore()).isEqualTo(0);
        assertThat(finishedMatch.getAwayScore()).isEqualTo(0);
        assertThat(finishedMatch.isFinished()).isTrue();
        assertThat(finishedMatch.getStartTime()).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("finishMatch - should throw MatchNotFoundException when match does not exist")
    void shouldThrowExceptionWhenFinishingNonExistentMatch() {
        var nonExistentMatchId = "invalid-id";

        assertThatThrownBy(() -> scoreboard.finishMatch(nonExistentMatchId))
                .isInstanceOf(MatchNotFoundException.class)
                .hasMessageContaining(nonExistentMatchId);
    }

    @Test
    @DisplayName("Get summary - should return only ongoing matches")
    void shouldGetSummary() throws InterruptedException {
        var homeTeam1 = createTeam("1", "HomeTeam1");
        var awayTeam1 = createTeam("2", "AwayTeam1");
        var homeTeam2 = createTeam("3", "HomeTeam2");
        var awayTeam2 = createTeam("4", "AwayTeam2");
        var homeTeam3 = createTeam("5", "HomeTeam3");
        var awayTeam3 = createTeam("6", "AwayTeam3");

        var match1 = scoreboard.startMatch(homeTeam1, awayTeam1);
        var match2 = scoreboard.startMatch(homeTeam2, awayTeam2);
        var match3 = scoreboard.startMatch(homeTeam3, awayTeam3);
        scoreboard.finishMatch(match1.getMatchId());

        Thread.sleep(10);
        var match4 = scoreboard.startMatch(homeTeam1, awayTeam1);

        var updatedMatch2 = scoreboard.updateScore(match2.getMatchId(), 2, 3);

        var summary = scoreboard.getSummary();

        assertThat(summary).containsExactly(updatedMatch2 ,match4, match3);
    }

    @Test
    @DisplayName("Get summary - should return empty list when no matches")
    void shouldGetEmptySummaryWhenNoMatches() {
        var summary = scoreboard.getSummary();

        assertThat(summary).isEmpty();
    }

    private static WorldCupScoreboard createScoreboard() {
        var matchRepository = new InMemoryMatchRepository();
        var sortingPolicy = new SortingByHighestScoreAndMostRecentlyStartedPolicy();
        return new WorldCupScoreboard(matchRepository, sortingPolicy);
    }

    private static Team createTeam(String id, String name) {
        return new Team(id, name, name.substring(0, 2).toUpperCase());
    }
}