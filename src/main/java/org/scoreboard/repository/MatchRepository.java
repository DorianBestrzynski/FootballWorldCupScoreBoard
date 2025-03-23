package org.scoreboard.repository;

import org.scoreboard.model.Match;

import java.util.Collection;
import java.util.Optional;

public interface MatchRepository {
    Match save(Match match);

    Match put(Match match);

    Optional<Match> findById(String matchId);

    Collection<Match> findAll();
}
