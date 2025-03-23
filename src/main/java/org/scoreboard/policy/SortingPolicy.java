package org.scoreboard.policy;

import java.util.Comparator;

public interface SortingPolicy<T> {

    Comparator<T> apply();
}
