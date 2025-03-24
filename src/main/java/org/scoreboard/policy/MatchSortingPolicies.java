package org.scoreboard.policy;

import org.scoreboard.model.Match;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class MatchSortingPolicies {

    public static Comparator<Match> highestScoringMatchesFirst() {
        return comparingInt(Match::getTotalScore).reversed();
    }

    public static Comparator<Match> mostRecentlyStartedMatchesFirst() {
        return comparing(Match::getStartTime).reversed();
    }
}
