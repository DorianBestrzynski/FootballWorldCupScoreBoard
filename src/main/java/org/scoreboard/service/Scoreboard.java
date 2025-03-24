package org.scoreboard.service;

import org.scoreboard.model.Match;
import org.scoreboard.model.MatchSummary;
import org.scoreboard.model.Team;

import java.util.List;

public interface Scoreboard {
    Match startMatch(Team homeTeam, Team awayTeam);

    Match updateScore(String matchId, int homeScore, int awayScore);

    Match finishMatch(String matchId);

    List<MatchSummary> getSummary();
}
