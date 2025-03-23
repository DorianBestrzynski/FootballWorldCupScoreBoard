package org.scoreboard.policy;

import org.scoreboard.model.Match;

import java.util.Comparator;

import static java.util.Comparator.comparing;

public class SortingByHighestScoreAndLastUpdatedPolicy implements SortingPolicy<Match> {
    @Override
    public Comparator<Match> apply() {
        return comparing(Match::totalScore).reversed()
                .thenComparing(comparing(Match::lastUpdated).reversed());
    }
}
