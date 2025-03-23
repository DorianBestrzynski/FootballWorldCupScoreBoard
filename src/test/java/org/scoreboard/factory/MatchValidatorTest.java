package org.scoreboard.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.scoreboard.exception.ValidationException;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchValidatorTest {

    private MatchValidator matchValidator;

    @BeforeEach
    void setUp() {
        matchValidator = new MatchValidator();
    }

    @Nested
    class TeamValidation {
        @Test
        void shouldThrowExceptionWhenHomeTeamIsNull() {
            var match = createMatch(null, new Team("2", "Away", "Away Team"), 0, 0);

            assertThatThrownBy(() -> matchValidator.validate(match))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Teams cannot be null.");
        }

        @Test
        void shouldThrowExceptionWhenAwayTeamIsNull() {
            var match = createMatch(new Team("1", "Home", "Home Team"), null, 0, 0);

            assertThatThrownBy(() -> matchValidator.validate(match))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Teams cannot be null.");
        }

        @Test
        void shouldThrowExceptionWhenTeamsAreTheSame() {
            var team = new Team("1", "Team", "Team Name");
            var match = createMatch(team, team, 0, 0);

            assertThatThrownBy(() -> matchValidator.validate(match))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Teams cannot be the same.");
        }
    }

    @Nested
    class ScoreValidation {
        @Test
        void shouldThrowExceptionWhenHomeScoreIsNegative() {
            var match = createMatch(new Team("1", "Home", "Home Team"), new Team("2", "Away", "Away Team"), -1, 0);

            assertThatThrownBy(() -> matchValidator.validate(match))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Scores cannot be negative.");
        }

        @Test
        void shouldThrowExceptionWhenAwayScoreIsNegative() {
            var match = createMatch(new Team("1", "Home", "Home Team"), new Team("2", "Away", "Away Team"), 0, -1);

            assertThatThrownBy(() -> matchValidator.validate(match))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Scores cannot be negative.");
        }
    }

    @Test
    void shouldPassValidationForValidMatch() {
        var match = createMatch(new Team("1", "Home", "Home Team"), new Team("2", "Away", "Away Team"), 1, 2);

        assertThatCode(() -> matchValidator.validate(match))
                .doesNotThrowAnyException();
    }

    private static Match createMatch(Team homeTeam, Team awayTeam, int homeScore, int awayScore) {
        return Match.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeScore(homeScore)
                .awayScore(awayScore)
                .build();
    }
}