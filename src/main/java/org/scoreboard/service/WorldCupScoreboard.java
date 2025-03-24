package org.scoreboard.service;

import lombok.RequiredArgsConstructor;
import org.scoreboard.exception.MatchNotFoundException;
import org.scoreboard.exception.OngoingMatchException;
import org.scoreboard.model.Match;
import org.scoreboard.model.Team;
import org.scoreboard.policy.SortingPolicy;
import org.scoreboard.repository.MatchRepository;

import java.util.List;

@RequiredArgsConstructor
public class WorldCupScoreboard implements Scoreboard {
    private final MatchRepository matchRepository;

    private final SortingPolicy<Match> sortingPolicy;

    @Override
    public Match startMatch(Team homeTeam, Team awayTeam) {
        var match = new Match(homeTeam, awayTeam);
        validateNoOngoingTeamMatches(homeTeam);
        validateNoOngoingTeamMatches(awayTeam);

        return matchRepository.save(match);
    }

    @Override
    public Match updateScore(String matchId, int homeScore, int awayScore) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);

        return matchRepository.put(match);
    }

    @Override
    public Match finishMatch(String matchId) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        match.finishMatch();

        return matchRepository.put(match);
    }

    @Override
    public List<Match> getSummary() {
        return matchRepository.findAll().stream()
                .filter(match -> !match.isFinished())
                .sorted(sortingPolicy.apply())
                .toList();
    }

    private void validateNoOngoingTeamMatches(Team team) {
        var ongoingMatches = matchRepository.findTeamMatches(team).stream()
                .filter(match -> !match.isFinished())
                .toList();
        if (!ongoingMatches.isEmpty()) {
            throw new OngoingMatchException(team.teamId());
        }
    }
}
