package com.yzy.voice.event;

/**
 * @author Rair
 * @date 2018/4/24
 * <p>
 * desc:
 */
public class PlayEvent {

    private boolean isComplete;

    public PlayEvent(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
