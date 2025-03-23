package org.scoreboard.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scoreboard.exception.MatchNotFoundException;
import org.scoreboard.factory.MatchFactory;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;
import org.scoreboard.policy.SortingPolicy;
import org.scoreboard.repository.MatchRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreboardTest {
    @Mock
    private MatchFactory matchFactory;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private SortingPolicy sortingPolicy;

    @InjectMocks
    private WorldCupScoreboard scoreboard;

    @Nested
    class StartMatch {
        @Test
        void shouldCreateAndSaveMatch() {
            var match = createMatch("match-1", false, 0, 0, Instant.now());
            when(matchFactory.create(any(), any(), any()))
                    .thenReturn(match);
            when(matchRepository.save(any()))
                    .thenReturn(match);

            var result = scoreboard.startMatch(match.homeTeam(), match.awayTeam());

            assertThat(result).isSameAs(match);
            verify(matchFactory).create(same(match.homeTeam()), same(match.awayTeam()), any());
            verify(matchRepository).save(match);
        }
    }

    @Nested
    class UpdateScore {
        @Test
        void shouldUpdateScoreAndSaveMatch() {
            var existingMatch = createMatch("match-1", false, 1, 0, Instant.now());
            var updatedMatch = createMatch("match-1", false, 2, 1, Instant.now());

            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.of(existingMatch));
            when(matchFactory.updateScore(any(), anyInt(), anyInt(), any()))
                    .thenReturn(updatedMatch);
            when(matchRepository.put(any()))
                    .thenReturn(updatedMatch);

            var result = scoreboard.updateScore("match-1", 2, 1);

            assertThat(result).isSameAs(updatedMatch);
            verify(matchFactory).updateScore(same(existingMatch), eq(2), eq(1), any());
            verify(matchRepository).put(updatedMatch);
        }

        @Test
        void shouldThrowExceptionIfMatchNotFound() {
            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreboard.updateScore("match-1", 2, 1))
                    .isInstanceOf(MatchNotFoundException.class)
                    .hasMessageContaining("match-1");

            verifyNoInteractions(matchFactory);
            verifyNoMoreInteractions(matchRepository);
        }
    }

    @Nested
    class FinishMatch {
        @Test
        void shouldFinishMatchAndSave() {
            var existingMatch = createMatch("match-1", false, 1, 1, Instant.now());
            var finishedMatch = createMatch("match-1", true, 1, 1, Instant.now());

            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.of(existingMatch));
            when(matchFactory.finishMatch(any(), any()))
                    .thenReturn(finishedMatch);
            when(matchRepository.put(any()))
                    .thenReturn(finishedMatch);

            var result = scoreboard.finishMatch("match-1");

            assertThat(result).isSameAs(finishedMatch);
            verify(matchFactory).finishMatch(same(existingMatch), any());
            verify(matchRepository).put(finishedMatch);
        }

        @Test
        void shouldThrowExceptionIfMatchNotFound() {
            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreboard.finishMatch("match-1"))
                    .isInstanceOf(MatchNotFoundException.class)
                    .hasMessageContaining("match-1");

            verifyNoInteractions(matchFactory);
            verifyNoMoreInteractions(matchRepository);
        }
    }

    @Nested
    class GetSummary {
        @Test
        void shouldFilterOutFinishedMatches() {
            var match1 = createMatch("match-1", false, 3, 2, Instant.now());
            var match2 = createMatch("match-2", false, 1, 0, Instant.now());
            var match3 = createMatch("match-3", true, 2, 2, Instant.now()); // Finished match, should be ignored

            when(matchRepository.findAll())
                    .thenReturn(List.of(match1, match2, match3));
            when(sortingPolicy.apply())
                    .thenReturn((_, _) -> 0);

            var result = scoreboard.getSummary();

            assertThat(result).containsExactly(match1, match2);
            verify(matchRepository).findAll();
            verify(sortingPolicy).apply();
        }
    }

    private static Match createMatch(String id, boolean isFinished, int homeScore, int awayScore, Instant lastUpdated) {
        return Match.builder()
                .matchId(id)
                .homeTeam(new Team("1", "Home", "Home Team"))
                .awayTeam(new Team("2", "Away", "Away Team"))
                .homeScore(homeScore)
                .awayScore(awayScore)
                .isFinished(isFinished)
                .lastUpdated(lastUpdated)
                .build();
    }
}