package org.scoreboard.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.scoreboard.exception.DomainValidationException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchTest {
    private static final Team DUMMY_HOME_TEAM = new Team("team-id-1", "Real Madrid", "RMA");
    private static final Team DUMMY_AWAY_TEAM = new Team("team-id-2", "Barcelona", "BAR");

    @Nested
    class CreateMatch {
        @Test
        void shouldCreateMatch() {
            var now = Instant.now();
            var homeTeam = new Team("team-id-1", "Real Madrid", "RMA");
            var awayTeam = new Team("team-id-2", "Barcelona", "BAR");

            Match match = new Match(homeTeam, awayTeam);

            assertThat(match.getMatchId()).isNotNull();
            assertThat(match.getHomeTeam()).isSameAs(homeTeam);
            assertThat(match.getAwayTeam()).isSameAs(awayTeam);
            assertThat(match.getHomeScore()).isEqualTo(0);
            assertThat(match.getAwayScore()).isEqualTo(0);
            assertThat(match.isFinished()).isEqualTo(false);
            assertThat(match.getStartTime()).isAfterOrEqualTo(now);
        }

        @Test
        void shouldThrowExceptionWhenHomeTeamIsNull() {
            assertThatThrownBy(() -> new Match(null, new Team("2", "Away", "Away Team")))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessage("Teams cannot be null.");
        }

        @Test
        void shouldThrowExceptionWhenAwayTeamIsNull() {
            assertThatThrownBy(() -> new Match(new Team("1", "Home", "Home Team"), null))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessage("Teams cannot be null.");
        }

        @Test
        void shouldThrowExceptionWhenTeamsAreTheSame() {
            var team = new Team("1", "Team", "Team Name");

            assertThatThrownBy(() -> new Match(team, team))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessage("Teams cannot be the same.");
        }
    }

    @Nested
    class SetScore {
        @Test
        void shouldSetHomeScore() {
            var match = new Match(DUMMY_HOME_TEAM, DUMMY_AWAY_TEAM);

            match.setHomeScore(10);

            assertThat(match.getHomeScore()).isEqualTo(10);

        }

        @Test
        void shouldSetAwayScore() {
            var match = new Match(DUMMY_HOME_TEAM, DUMMY_AWAY_TEAM);

            match.setAwayScore(10);

            assertThat(match.getAwayScore()).isEqualTo(10);

        }

        @Test
        void shouldThrowExceptionWhenHomeScoreIsNegative() {
            var match = new Match(DUMMY_HOME_TEAM, DUMMY_AWAY_TEAM);

            assertThatThrownBy(() -> match.setHomeScore(-1))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessage("Scores cannot be negative.");
        }

        @Test
        void shouldThrowExceptionWhenAwayScoreIsNegative() {
            var match = new Match(DUMMY_HOME_TEAM, DUMMY_AWAY_TEAM);

            assertThatThrownBy(() -> match.setAwayScore(-1))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessage("Scores cannot be negative.");
        }
    }

    @Nested
    class FinishMatch {
        @Test
        void shouldSetMatchAsFinished() {
            var match = new Match(DUMMY_HOME_TEAM, DUMMY_AWAY_TEAM);

            match.finishMatch();

            assertThat(match.isFinished()).isEqualTo(true);
        }

        @Test
        void shouldThrowExceptionWhenMatchAlreadyFinished() {
            var match = new Match(DUMMY_HOME_TEAM, DUMMY_AWAY_TEAM);

            match.finishMatch();

            assertThatThrownBy(match::finishMatch)
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessage("Match is already finished.");
        }
    }

    @Nested
    class TotalScore {
        @Test
        void shouldCalculateTotalScore() {
            var match = new Match(DUMMY_HOME_TEAM, DUMMY_AWAY_TEAM);
            match.setHomeScore(1);
            match.setAwayScore(2);

            var result = match.getTotalScore();

            assertThat(result).isEqualTo(3);
        }
    }
}