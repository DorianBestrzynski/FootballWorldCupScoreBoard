package org.scoreboard.repository;

import org.scoreboard.model.Match;
import org.scoreboard.model.Team;

import java.util.*;

import static java.util.Optional.ofNullable;

public class InMemoryMatchRepository implements MatchRepository {
    private final Map<String, Match> matches = new HashMap<>();

    @Override
    public Match save(Match match) {
        if (matches.containsKey(match.getMatchId())) {
            throw new IllegalArgumentException("There is already a match with provided id: %s".formatted(match.getMatchId()));
        }
        matches.put(match.getMatchId(), match);
        return match;
    }

    @Override
    public Match put(Match match) {
        matches.put(match.getMatchId(), match);
        return match;
    }

    @Override
    public Optional<Match> findById(String matchId) {
        return ofNullable(matches.get(matchId));
    }

    @Override
    public List<Match> findAll() {
        return new ArrayList<>(matches.values());
    }

    @Override
    public List<Match> findTeamMatches(Team team) {
        return matches.values().stream()
                .filter(match -> isTeamPresentInMatch(team, match))
                .toList();
    }

    private boolean isTeamPresentInMatch(Team team, Match match) {
        return match.getHomeTeam().teamId().equals(team.teamId()) || match.getAwayTeam().teamId().equals(team.teamId());
    }
}
