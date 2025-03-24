package org.scoreboard.repository;

import org.scoreboard.model.Match;

import java.util.*;

import static java.util.Optional.ofNullable;

public class InMemoryMatchRepository implements MatchRepository {
    private final Map<String, Match> matches = new HashMap<>();
    private final Set<String> teamsWithLiveMatches = new HashSet<>();

    @Override
    public Match save(Match match) {
        if (matches.containsKey(match.getMatchId())) {
            throw new IllegalArgumentException("There is already a match with provided id: %s".formatted(match.getMatchId()));
        }
        matches.put(match.getMatchId(), match);
        teamsWithLiveMatches.add(match.getHomeTeam().teamId());
        teamsWithLiveMatches.add(match.getAwayTeam().teamId());
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
    public void removeTeamsFromActiveMatches(String homeTeamId, String awayTeamId) {
        teamsWithLiveMatches.remove(homeTeamId);
        teamsWithLiveMatches.remove(awayTeamId);
    }

    @Override
    public boolean isTeamParticipatingInLiveMatch(String teamId) {
        return teamsWithLiveMatches.contains(teamId);
    }
}
