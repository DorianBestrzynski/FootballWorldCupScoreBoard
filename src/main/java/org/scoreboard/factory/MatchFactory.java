package org.scoreboard.factory;

import lombok.RequiredArgsConstructor;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class MatchFactory {
    private final MatchValidator matchValidator;

    public Match create(Team homeTeam, Team awayTeam, Instant lastUpdated) {
        var match = Match.builder()
                .matchId(UUID.randomUUID().toString())
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeScore(0)
                .awayScore(0)
                .lastUpdated(lastUpdated)
                .isFinished(false)
                .build();
        matchValidator.validate(match);

        return match;
    }

    public Match updateScore(Match match, int homeScore, int awayScore, Instant lastUpdated) {
        var updatedMatch = match.toBuilder()
                .homeScore(homeScore)
                .awayScore(awayScore)
                .lastUpdated(lastUpdated)
                .build();
        matchValidator.validate(updatedMatch);
        return updatedMatch;
    }

    public Match finishMatch(Match match, Instant lastUpdated) {
        var updatedMatch = match.toBuilder()
                .isFinished(true)
                .lastUpdated(lastUpdated)
                .build();
        matchValidator.validate(updatedMatch);
        return updatedMatch;
    }
}
