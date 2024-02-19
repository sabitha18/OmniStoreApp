package com.armada.storeapp.ui.utils.ViewPagerIndicator;

/**
 * Created by hrskrs on 10/16/17.
 */

public class Dot {
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    enum State {
        SMALL,
        MEDIUM,
        INACTIVE,
        ACTIVE
    }

}
