package org.scoreboard.repository;

import org.scoreboard.model.Match;

import java.util.*;

import static java.util.Optional.ofNullable;

public class InMemoryMatchRepository implements MatchRepository {
    private final Map<String, Match> matches = new HashMap<>();

    @Override
    public Match save(Match match) {
        if (matches.containsKey(match.matchId())) {
            throw new IllegalArgumentException("There is already a match with provided id: %s".formatted(match.matchId()));
        }
        matches.put(match.matchId(), match);
        return match;
    }

    @Override
    public Match put(Match match) {
        matches.put(match.matchId(), match);
        return match;
    }

    @Override
    public Optional<Match> findById(String matchId) {
        return ofNullable(matches.get(matchId));
    }

    @Override
    public Collection<Match> findAll() {
        return matches.values();
    }
}
