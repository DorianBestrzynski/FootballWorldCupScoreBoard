package org.scoreboard.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.scoreboard.exception.ValidationException;

import java.time.Instant;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class Match {
    private final String matchId;
    private final Team homeTeam;
    private final Team awayTeam;
    private int homeScore;
    private int awayScore;
    private boolean isFinished;
    private final Instant startTime;

    public Match(String matchId, Team homeTeam, Team awayTeam, int homeScore, int awayScore, boolean isFinished, Instant startTime) {
        validateTeams(homeTeam, awayTeam);
        validateScore(homeScore);
        validateScore(awayScore);
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.isFinished = isFinished;
        this.startTime = startTime;
    }

    public Match(Team homeTeam, Team awayTeam) {
        validateTeams(homeTeam, awayTeam);
        this.matchId = UUID.randomUUID().toString();
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
        this.isFinished = false;
        this.startTime = Instant.now();
    }

    public void setHomeScore(int homeScore) {
        validateScore(homeScore);
        this.homeScore = homeScore;
    }

    public void setAwayScore(int awayScore) {
        validateScore(awayScore);
        this.awayScore = awayScore;
    }

    public void finishMatch() {
        validateIsFinished();
        this.isFinished = true;
    }

    public int totalScore() {
        return homeScore + awayScore;
    }

    private static void validateTeams(Team homeTeam, Team awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new ValidationException("Teams cannot be null.");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new ValidationException("Teams cannot be the same.");
        }
    }

    private void validateScore(int score) {
        if (score < 0) {
            throw new ValidationException("Scores cannot be negative.");
        }
    }

    private void validateIsFinished() {
        if (isFinished) {
            throw new ValidationException("Match is already finished.");
        }
    }
}
