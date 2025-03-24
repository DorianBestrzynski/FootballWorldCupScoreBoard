package org.scoreboard.exception;

public class OngoingMatchException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "This team: %s has already ongoing match";

    public OngoingMatchException(String teamId) {
        super(MESSAGE_TEMPLATE.formatted(teamId));
    }
}
