package org.scoreboard.factory;

import org.scoreboard.exception.ValidationException;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

public class MatchValidator {

    public void validate(Match match) {
        validateTeams(match.homeTeam(), match.awayTeam());
        validateScore(match.homeScore(), match.awayScore());
    }

    private void validateTeams(Team homeTeam, Team awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new ValidationException("Teams cannot be null.");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new ValidationException("Teams cannot be the same.");
        }
    }

    private void validateScore(int homeScore, int awayScore) {
        if (homeScore < 0 || awayScore < 0) {
            throw new ValidationException("Scores cannot be negative.");
        }
    }
}
