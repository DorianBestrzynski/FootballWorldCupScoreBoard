package org.scoreboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scoreboard.exception.MatchNotFoundException;
import org.scoreboard.exception.ValidationException;
import org.scoreboard.factory.MatchFactory;
import org.scoreboard.factory.MatchValidator;
import org.scoreboard.model.Team;
import org.scoreboard.policy.SortingPolicy;
import org.scoreboard.repository.InMemoryMatchRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoreboardComponentTest {
    private final Scoreboard scoreboard = createScoreboard();

    @Test
    @DisplayName("Start match - should create and store match")
    void shouldStartMatch() {
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);

        assertThat(match).isNotNull();
        assertThat(match.homeTeam()).isEqualTo(homeTeam);
        assertThat(match.awayTeam()).isEqualTo(awayTeam);
        assertThat(match.homeScore()).isEqualTo(0);
        assertThat(match.awayScore()).isEqualTo(0);
        assertThat(match.isFinished()).isFalse();
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
    @DisplayName("Update score - should modify existing match")
    void shouldUpdateScore() throws InterruptedException {
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);
        var matchId = match.matchId();
        var timestamp = match.lastUpdated();

        Thread.sleep(10);

        var updatedMatch = scoreboard.updateScore(matchId, 2, 1);

        assertThat(updatedMatch).isNotNull();
        assertThat(updatedMatch.matchId()).isEqualTo(matchId);
        assertThat(updatedMatch.homeTeam()).isEqualTo(homeTeam);
        assertThat(updatedMatch.awayTeam()).isEqualTo(awayTeam);
        assertThat(updatedMatch.homeScore()).isEqualTo(2);
        assertThat(updatedMatch.awayScore()).isEqualTo(1);
        assertThat(updatedMatch.isFinished()).isFalse();
        assertThat(updatedMatch.lastUpdated()).isAfter(timestamp);
    }

    @Test
    @DisplayName("validateScore - should throw ValidationException for negative scores")
    void shouldThrowExceptionWhenScoresAreNegative() {
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);
        var matchId = match.matchId();

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
    void shouldFinishMatch() throws InterruptedException {
        var homeTeam = createTeam("1", "HomeTeam");
        var awayTeam = createTeam("2", "AwayTeam");

        var match = scoreboard.startMatch(homeTeam, awayTeam);
        var matchId = match.matchId();
        var timestamp = match.lastUpdated();

        Thread.sleep(10);

        var finishedMatch = scoreboard.finishMatch(matchId);

        assertThat(finishedMatch).isNotNull();
        assertThat(finishedMatch.matchId()).isEqualTo(matchId);
        assertThat(finishedMatch.homeTeam()).isEqualTo(homeTeam);
        assertThat(finishedMatch.awayTeam()).isEqualTo(awayTeam);
        assertThat(finishedMatch.homeScore()).isEqualTo(0);
        assertThat(finishedMatch.awayScore()).isEqualTo(0);
        assertThat(finishedMatch.isFinished()).isTrue();
        assertThat(finishedMatch.lastUpdated()).isAfter(timestamp);
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

        var match1 = scoreboard.startMatch(homeTeam1, awayTeam1);
        var match2 = scoreboard.startMatch(homeTeam2, awayTeam2);
        var match3 = scoreboard.startMatch(homeTeam2, awayTeam1);
        var match4 = scoreboard.startMatch(homeTeam1, awayTeam2);

        scoreboard.updateScore(match4.matchId(), 2, 1);
        var updatedMatch3 = scoreboard.updateScore(match3.matchId(), 3, 1);
        Thread.sleep(10);
        var updatedMatch4 = scoreboard.updateScore(match4.matchId(), 3, 1);
        scoreboard.finishMatch(match1.matchId());

        var summary = scoreboard.getSummary();

        assertThat(summary).containsExactly(updatedMatch4, updatedMatch3, match2);
    }

    @Test
    @DisplayName("Get summary - should return empty list when no matches")
    void shouldGetEmptySummaryWhenNoMatches() {
        var summary = scoreboard.getSummary();

        assertThat(summary).isEmpty();
    }

    private static WorldCupScoreboard createScoreboard() {
        MatchValidator matchValidator = new MatchValidator();
        MatchFactory matchFactory = new MatchFactory(matchValidator);
        InMemoryMatchRepository matchRepository = new InMemoryMatchRepository();
        SortingPolicy sortingPolicy = SortingPolicy.create();
        return new WorldCupScoreboard(matchFactory, matchRepository, sortingPolicy);
    }

    private static Team createTeam(String id, String name) {
        return new Team(id, name, name.substring(0, 2).toUpperCase());
    }
}