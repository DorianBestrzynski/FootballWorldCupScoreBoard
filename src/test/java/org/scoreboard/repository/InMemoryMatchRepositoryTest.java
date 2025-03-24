package org.scoreboard.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryMatchRepositoryTest {
    private InMemoryMatchRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMatchRepository();
    }

    @Nested
    class SaveMethod {
        @Test
        void shouldSaveMatchAndAddActiveTeamIdsSuccessfully() {
            var match = match("match-1", "home-team-id", "away-team-id");

            var result = repository.save(match);

            assertThat(result).isSameAs(match);
            assertThat(repository.findById("match-1")).contains(match);
            assertThat(repository.isTeamParticipatingInLiveMatch("home-team-id")).isTrue();
            assertThat(repository.isTeamParticipatingInLiveMatch("away-team-id")).isTrue();
        }

        @Test
        void shouldThrowExceptionWhenMatchIdAlreadyExists() {
            var match = match("match-1");
            repository.save(match);

            var duplicateMatch = match("match-1");

            assertThatThrownBy(() -> repository.save(duplicateMatch))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("match-1");
        }
    }

    @Nested
    class PutMethod {
        @Test
        void shouldPutMatchSuccessfully() {
            var match = match("match-1");

            var result = repository.put(match);

            assertThat(result).isSameAs(match);
            assertThat(repository.findById("match-1")).contains(match);
        }

        @Test
        void shouldOverrideExistingMatch() {
            var match1 = match("match-1");
            repository.put(match1);

            var match2 = match("match-1");
            repository.put(match2);

            assertThat(repository.findById("match-1")).contains(match2);
        }
    }

    @Nested
    class RemoveTeamsFromActiveList {
        @Test
        void shouldRemoveTeamsFromActiveListSuccessfully() {
            var match = match("match-1", "home-team-id", "away-team-id");

            repository.save(match);

            repository.removeTeamsFromActiveMatches("home-team-id", "away-team-id");

            assertThat(repository.isTeamParticipatingInLiveMatch("home-team-id")).isFalse();
            assertThat(repository.isTeamParticipatingInLiveMatch("away-team-id")).isFalse();
        }

        @Test
        void shouldDoNothingWhenNoTeamToBeRemoved() {
            repository.removeTeamsFromActiveMatches("home-team-id", "away-team-id");

            assertThat(repository.isTeamParticipatingInLiveMatch("home-team-id")).isFalse();
            assertThat(repository.isTeamParticipatingInLiveMatch("away-team-id")).isFalse();
        }
    }

    @Nested
    class IsTeamParticipatingInLiveMatch {
        @Test
        void shouldReturnTrueIfTeamIsParticipatingInLiveMatch() {
            var match = match("match-1", "home-team-id", "away-team-id");

            repository.save(match);

            var result = repository.isTeamParticipatingInLiveMatch("home-team-id");

            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnFalseIfTeamIsNotParticipatingInLiveMatch() {
            var result = repository.isTeamParticipatingInLiveMatch("home-team-id");

            assertThat(result).isFalse();
        }
    }

    @Nested
    class FindByIdMethod {
        @Test
        void shouldReturnMatchIfExists() {
            var match = match("match-1");
            repository.save(match);

            var result = repository.findById("match-1");

            assertThat(result).contains(match);
        }

        @Test
        void shouldReturnEmptyOptionalIfMatchNotFound() {
            var result = repository.findById("non-existent");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindAllMethod {
        @Test
        void shouldReturnAllMatches() {
            var match1 = match("match-1");
            var match2 = match("match-2");
            repository.save(match1);
            repository.save(match2);

            assertThat(repository.findAll()).containsExactlyInAnyOrder(match1, match2);
        }

        @Test
        void shouldReturnEmptyCollectionIfNoMatches() {
            assertThat(repository.findAll()).isEmpty();
        }
    }

    private Match match(String matchId) {
        return new Match(
                matchId,
                new Team("home-id", "name", "displayName"),
                new Team("away-id", "name", "displayName"),
                0,
                0,
                false,
                Instant.now());
    }

    private Match match(String matchId, String homeTeamId, String awayTeamId) {
        return new Match(
                matchId,
                new Team(homeTeamId, "name", "displayName"),
                new Team(awayTeamId, "name", "displayName"),
                0,
                0,
                false,
                Instant.now());
    }
}