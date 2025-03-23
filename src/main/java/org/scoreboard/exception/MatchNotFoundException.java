package org.scoreboard.exception;

public class MatchNotFoundException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Match not found for id: %s";

    public MatchNotFoundException(String matchId) {
        super(MESSAGE_TEMPLATE.formatted(matchId));
    }
}
