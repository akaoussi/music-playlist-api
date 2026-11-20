package com.musicstream.playlist.domain.model;

import java.util.EnumSet;
import java.util.Set;

public enum Genre {
    ROCK, POP, JAZZ, BLUES;

    private static final double NEIGHBOUR_COMPATIBILITY = 0.6;
    private static final double UNRELATED_COMPATIBILITY = 0.1;

    private Set<Genre> declaredNeighbours() {
        return switch (this) {
            case ROCK -> EnumSet.of(BLUES, POP);
            case JAZZ -> EnumSet.of(BLUES);
            case POP, BLUES -> EnumSet.noneOf(Genre.class);
        };
    }

    public double compatibilityWith(Genre other) {
        if (this == other) {
            return 1.0;
        }
        boolean neighbours = declaredNeighbours().contains(other) || other.declaredNeighbours().contains(this);
        return neighbours ? NEIGHBOUR_COMPATIBILITY : UNRELATED_COMPATIBILITY;
    }
}
