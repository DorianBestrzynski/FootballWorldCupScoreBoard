package org.scoreboard.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchTest {

    @Test
    void shouldCalculateTotalScore() {
        var match = Match.builder()
                .homeScore(1)
                .awayScore(2)
                .build();

        var result = match.totalScore();

        assertThat(result).isEqualTo(3);
    }
}