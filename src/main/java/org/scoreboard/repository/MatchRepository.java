package org.scoreboard.repository;

import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {
    Match save(Match match);

    Match put(Match match);

    Optional<Match> findById(String matchId);

    List<Match> findAll();

    List<Match> findTeamMatches(Team team);
}
