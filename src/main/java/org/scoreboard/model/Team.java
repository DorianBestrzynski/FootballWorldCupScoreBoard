package org.scoreboard.model;

import lombok.Builder;

@Builder
public record Team(
        String teamId,
        String name,
        String displayName) {
}
