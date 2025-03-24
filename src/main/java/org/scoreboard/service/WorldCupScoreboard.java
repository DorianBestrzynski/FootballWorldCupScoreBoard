package org.scoreboard.service;

import lombok.RequiredArgsConstructor;
import org.scoreboard.exception.MatchNotFoundException;
import org.scoreboard.exception.OngoingMatchException;
import org.scoreboard.model.Match;
import org.scoreboard.model.MatchSummary;
import org.scoreboard.model.Team;
import org.scoreboard.repository.InMemoryMatchRepository;
import org.scoreboard.repository.MatchRepository;

import java.util.Comparator;
import java.util.List;

import static org.scoreboard.policy.MatchSortingPolicies.highestScoringMatchesFirst;
import static org.scoreboard.policy.MatchSortingPolicies.mostRecentlyStartedMatchesFirst;

@RequiredArgsConstructor
public class WorldCupScoreboard implements Scoreboard {
    private final MatchRepository matchRepository;

    private final Comparator<Match> sortingPolicy;

    public static Scoreboard create() {
        return new WorldCupScoreboard(
                new InMemoryMatchRepository(),
                highestScoringMatchesFirst()
                        .thenComparing(mostRecentlyStartedMatchesFirst()));
    }

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

        matchRepository.removeTeamsFromActiveMatches(match.getHomeTeamId(), match.getAwayTeamId());
        return matchRepository.put(match);
    }

    @Override
    public List<MatchSummary> getSummary() {
        return matchRepository.findAll().stream()
                .filter(match -> !match.isFinished())
                .sorted(sortingPolicy)
                .map(MatchSummary::generateSummary)
                .toList();
    }

    private void validateNoOngoingTeamMatches(Team team) {
        if (matchRepository.isTeamParticipatingInLiveMatch(team.teamId())) {
            throw new OngoingMatchException(team.teamId());
        }
    }
}
