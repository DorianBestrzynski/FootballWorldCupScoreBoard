package org.scoreboard.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scoreboard.exception.MatchNotFoundException;
import org.scoreboard.exception.OngoingMatchException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorldCupScoreboardTest {
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private SortingPolicy<Match> sortingPolicy;

    @InjectMocks
    private WorldCupScoreboard scoreboard;

    @Nested
    class StartMatch {
        @Test
        void shouldCreateAndSaveMatch() {
            var homeTeam = new Team("1", "Home", "Home Team");
            var awayTeam = new Team("2", "Away", "Away Team");

            var match = createMatch(homeTeam, awayTeam);
            when(matchRepository.save(any()))
                    .thenReturn(match);

            var result = scoreboard.startMatch(homeTeam, awayTeam);

            assertThat(result).isSameAs(match);
            verify(matchRepository, times(1)).save(any(Match.class));
        }

        @Test
        void shouldThrowExceptionWhenTeamAlreadyHasOngoingMatch() {
            var homeTeam = new Team("1", "Home", "Home Team");
            var awayTeam = new Team("2", "Away", "Away Team");

            var match = createMatch(homeTeam, awayTeam);
            when(matchRepository.findTeamMatches(homeTeam))
                    .thenReturn(List.of());
            when(matchRepository.findTeamMatches(awayTeam))
                    .thenReturn(List.of(match));

            assertThatThrownBy(() -> scoreboard.startMatch(homeTeam, awayTeam))
                    .isInstanceOf(OngoingMatchException.class)
                    .hasMessageContaining("2");
        }
    }

    @Nested
    class UpdateScore {
        @Test
        void shouldUpdateScoreAndSaveMatch() {
            var existingMatch = createMatch("match-1");
            var updatedMatch = createMatch("match-1");

            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.of(existingMatch));
            when(matchRepository.put(any()))
                    .thenReturn(updatedMatch);

            var result = scoreboard.updateScore("match-1", 2, 1);

            assertThat(result).isSameAs(updatedMatch);
            verify(matchRepository, times(1)).put(any(Match.class));
        }

        @Test
        void shouldThrowExceptionIfMatchNotFound() {
            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreboard.updateScore("match-1", 2, 1))
                    .isInstanceOf(MatchNotFoundException.class)
                    .hasMessageContaining("match-1");

            verifyNoMoreInteractions(matchRepository);
        }
    }

    @Nested
    class FinishMatch {
        @Test
        void shouldFinishMatchAndSave() {
            var existingMatch = createMatch("match-1");
            var finishedMatch = createMatch("match-1");
            finishedMatch.finishMatch();

            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.of(existingMatch));
            when(matchRepository.put(any()))
                    .thenReturn(finishedMatch);

            var result = scoreboard.finishMatch("match-1");

            assertThat(result).isSameAs(finishedMatch);
            verify(matchRepository).put(finishedMatch);
        }

        @Test
        void shouldThrowExceptionIfMatchNotFound() {
            when(matchRepository.findById("match-1"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreboard.finishMatch("match-1"))
                    .isInstanceOf(MatchNotFoundException.class)
                    .hasMessageContaining("match-1");

            verifyNoMoreInteractions(matchRepository);
        }
    }

    @Nested
    class GetSummary {
        @Test
        void shouldFilterOutFinishedMatches() {
            var match1 = createMatch("match-1");
            var match2 = createMatch("match-2");
            var match3 = createMatch("match-3");
            match3.finishMatch();

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

    private static Match createMatch(Team homeTeam, Team awayTeam) {
        return new Match(homeTeam, awayTeam);
    }

    private Match createMatch(String matchId) {
        return new Match(
                matchId,
                new Team("home-id", "name", "displayName"),
                new Team("away-id", "name", "displayName"),
                0,
                0,
                false,
                Instant.now());
    }
}