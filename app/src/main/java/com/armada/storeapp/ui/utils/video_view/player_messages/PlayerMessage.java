package com.armada.storeapp.ui.utils.video_view.player_messages;

import com.armada.storeapp.ui.utils.Logger;
import com.armada.storeapp.ui.utils.video_view.PlayerMessageState;
import com.armada.storeapp.ui.utils.video_view.VideoPlayerManagerCallback;
import com.armada.storeapp.ui.utils.video_view.VideoPlayerView;

/**
 * This is generic interface for PlayerMessage
 */
public abstract class PlayerMessage implements Message {

    private static final String TAG = PlayerMessage.class.getSimpleName();
    private static final boolean SHOW_LOGS = true;
    private final VideoPlayerView mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public PlayerMessage(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        mCurrentPlayer = currentPlayer;
        mCallback = callback;
    }

    protected final PlayerMessageState getCurrentState() {
        return mCallback.getCurrentPlayerState();
    }

    @Override
    public final void polledFromQueue() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateBefore());
    }

    @Override
    public final void messageFinished() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateAfter());
    }

    public final void runMessage() {
        if (SHOW_LOGS) Logger.v(TAG, ">> runMessage, " + getClass().getSimpleName());
        performAction(mCurrentPlayer);
        if (SHOW_LOGS) Logger.v(TAG, "<< runMessage, " + getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected abstract void performAction(VideoPlayerView currentPlayer);

    protected abstract PlayerMessageState stateBefore();

    protected abstract PlayerMessageState stateAfter();

}
