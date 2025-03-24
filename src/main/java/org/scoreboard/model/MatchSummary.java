package org.scoreboard.model;

public record MatchSummary(String summary) {

    public static MatchSummary generateSummary(Match match) {
        return new MatchSummary("%s %d - %s %d".formatted(match.getHomeTeam().name(), match.getHomeScore(), match.getAwayTeam().name(), match.getAwayScore()));
    }
}
