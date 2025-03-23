package org.scoreboard.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.scoreboard.model.Match;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchRepositoryTest {
    private InMemoryMatchRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMatchRepository();
    }

    @Nested
    class SaveMethod {
        @Test
        void shouldSaveMatchSuccessfully() {
            var match = Match.builder().matchId("match-1").build();

            var result = repository.save(match);

            assertThat(result).isSameAs(match);
            assertThat(repository.findById("match-1")).contains(match);
        }

        @Test
        void shouldThrowExceptionWhenMatchIdAlreadyExists() {
            var match = Match.builder().matchId("match-1").build();
            repository.save(match);

            var duplicateMatch = Match.builder().matchId("match-1").build();

            assertThatThrownBy(() -> repository.save(duplicateMatch))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("match-1");
        }
    }

    @Nested
    class PutMethod {
        @Test
        void shouldPutMatchSuccessfully() {
            var match = Match.builder().matchId("match-1").build();

            var result = repository.put(match);

            assertThat(result).isSameAs(match);
            assertThat(repository.findById("match-1")).contains(match);
        }

        @Test
        void shouldOverrideExistingMatch() {
            var match1 = Match.builder().matchId("match-1").homeScore(1).build();
            repository.put(match1);

            var match2 = Match.builder().matchId("match-1").homeScore(3).build();
            repository.put(match2);

            assertThat(repository.findById("match-1")).contains(match2);
        }
    }

    @Nested
    class FindByIdMethod {
        @Test
        void shouldReturnMatchIfExists() {
            Match match = Match.builder().matchId("match-1").build();
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
            var match1 = Match.builder().matchId("match-1").build();
            var match2 = Match.builder().matchId("match-2").build();
            repository.save(match1);
            repository.save(match2);

            assertThat(repository.findAll()).containsExactlyInAnyOrder(match1, match2);
        }

        @Test
        void shouldReturnEmptyCollectionIfNoMatches() {
            assertThat(repository.findAll()).isEmpty();
        }
    }
}