package org.scoreboard.model;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Match(
        String matchId,
        Team homeTeam,
        Team awayTeam,
        int homeScore,
        int awayScore,
        Instant lastUpdated,
        boolean isFinished) {

    public int totalScore() {
        return homeScore + awayScore;
    }
}
