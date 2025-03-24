package org.scoreboard.repository;

import org.scoreboard.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {
    Match save(Match match);

    Match put(Match match);

    Optional<Match> findById(String matchId);

    List<Match> findAll();

    void removeTeamsFromActiveMatches(String homeTeamId, String awayTeamId);

    boolean isTeamParticipatingInLiveMatch(String teamId);
}
