package com.Y1fel.JoJoPlagueSurge.entity.custom.stand;

public record StandFollowProfile(
        double backDistance,
        double sideDistance,
        double heightOffset,
        boolean usePitchHeightOffset
) {
    public static final StandFollowProfile DEFAULT_LEFT_BACK =
            new StandFollowProfile(0.85D, 0.65D, 0.80D, true);
}
