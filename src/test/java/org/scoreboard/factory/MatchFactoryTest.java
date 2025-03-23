package org.scoreboard.factory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scoreboard.exception.ValidationException;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchFactoryTest {
    @Mock
    private MatchValidator matchValidator;

    @InjectMocks
    private MatchFactory matchFactory;

    @Nested
    class CreateMatch {
        @Test
        void shouldGenerateRandomUUID() {
            var result = matchFactory.create(null, null, null);

            assertThat(result.matchId()).isNotBlank();
        }

        @Test
        void shouldSetHomeTeamAndAwayTeam() {
            var homeTeam = Team.builder().teamId("team-1").build();
            var awayTeam = Team.builder().teamId("team-2").build();
            var result = matchFactory.create(homeTeam, awayTeam, null);

            assertThat(result.homeTeam()).isSameAs(homeTeam);
            assertThat(result.awayTeam()).isSameAs(awayTeam);
        }

        @Test
        void shouldSetHomeAndAwayScoreAsZero() {
            var result = matchFactory.create(null, null, null);

            assertThat(result.homeScore()).isEqualTo(0);
            assertThat(result.awayScore()).isEqualTo(0);
        }

        @Test
        void shouldSetLastUpdated() {
            var lastUpdated = Instant.now();
            var result = matchFactory.create(null, null, lastUpdated);

            assertThat(result.lastUpdated()).isSameAs(lastUpdated);
        }

        @Test
        void shouldSetIsFinishedToFalse() {
            var result = matchFactory.create(null, null, null);

            assertThat(result.isFinished()).isFalse();
        }

        @Test
        void shouldValidateMatch() {
            var result = matchFactory.create(null, null, null);

            verify(matchValidator).validate(result);
        }

        @Test
        void shouldPropagateValidationError() {
            doThrow(new ValidationException("Invalid match"))
                    .when(matchValidator).validate(any(Match.class));

            assertThatThrownBy(() -> matchFactory.create(null, null, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Invalid match");
        }
    }

    @Nested
    class UpdateScore {
        @Test
        void shouldUpdateScores() {
            var match = Match.builder().homeScore(1).awayScore(2).build();
            var updatedMatch = matchFactory.updateScore(match, 3, 4, Instant.now());

            assertThat(updatedMatch.homeScore()).isEqualTo(3);
            assertThat(updatedMatch.awayScore()).isEqualTo(4);
        }

        @Test
        void shouldCallValidator() {
            var match = Match.builder().homeScore(1).awayScore(2).build();
            var updatedMatch = matchFactory.updateScore(match, 3, 4, Instant.now());

            verify(matchValidator).validate(updatedMatch);
        }

        @Test
        void shouldPropagateValidationError() {
            var match = Match.builder().homeScore(1).awayScore(2).build();

            doThrow(new ValidationException("Invalid score"))
                    .when(matchValidator).validate(any(Match.class));

            assertThatThrownBy(() -> matchFactory.updateScore(match, -1, 2, Instant.now()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Invalid score");
        }
    }

    @Nested
    class FinishMatch {
        @Test
        void shouldMarkMatchAsFinished() {
            var match = Match.builder().isFinished(false).build();
            var updatedMatch = matchFactory.finishMatch(match, Instant.now());

            assertThat(updatedMatch.isFinished()).isTrue();
        }

        @Test
        void shouldCallValidator() {
            var match = Match.builder().isFinished(false).build();
            var updatedMatch = matchFactory.finishMatch(match, Instant.now());

            verify(matchValidator).validate(updatedMatch);
        }

        @Test
        void shouldPropagateValidationError() {
            var match = Match.builder().isFinished(false).build();

            doThrow(new ValidationException("Invalid match state"))
                    .when(matchValidator).validate(any(Match.class));

            assertThatThrownBy(() -> matchFactory.finishMatch(match, Instant.now()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Invalid match state");
        }
    }
}