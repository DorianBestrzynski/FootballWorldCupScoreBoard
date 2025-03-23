package org.scoreboard.policy;

import org.scoreboard.model.Match;

import java.util.Comparator;

public interface SortingPolicy {

    Comparator<Match> apply();

    static SortingPolicy create() {
        return new SortingByHighestScoreAndLastUpdatedPolicy();
    }
}
