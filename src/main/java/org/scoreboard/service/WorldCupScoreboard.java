package org.scoreboard.service;

import lombok.RequiredArgsConstructor;
import org.scoreboard.exception.MatchNotFoundException;
import org.scoreboard.factory.MatchFactory;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;
import org.scoreboard.policy.SortingPolicy;
import org.scoreboard.repository.MatchRepository;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class WorldCupScoreboard implements Scoreboard {
    private final MatchFactory matchFactory;

    private final MatchRepository matchRepository;

    private final SortingPolicy<Match> sortingPolicy;

    @Override
    public Match startMatch(Team homeTeam, Team awayTeam) {
        var match = matchFactory.create(homeTeam, awayTeam, Instant.now());
        return matchRepository.save(match);
    }

    @Override
    public Match updateScore(String matchId, int homeScore, int awayScore) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        var updatedMatch = matchFactory.updateScore(match, homeScore, awayScore, Instant.now());
        return matchRepository.put(updatedMatch);
    }

    @Override
    public Match finishMatch(String matchId) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        var finishedMatch = matchFactory.finishMatch(match, Instant.now());

        return matchRepository.put(finishedMatch);
    }

    @Override
    public List<Match> getSummary() {
        return matchRepository.findAll().stream()
                .filter(match -> !match.isFinished())
                .sorted(sortingPolicy.apply())
                .toList();
    }
}
