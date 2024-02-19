package com.armada.storeapp.ui.utils.video_view.player_messages;

import com.armada.storeapp.ui.utils.video_view.PlayerMessageState;
import com.armada.storeapp.ui.utils.video_view.VideoPlayerManagerCallback;
import com.armada.storeapp.ui.utils.video_view.VideoPlayerView;

/**
 * This PlayerMessage clears MediaPlayer instance that was used inside {@link VideoPlayerView}
 */
public class ClearPlayerInstance extends PlayerMessage {

    public ClearPlayerInstance(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.clearPlayerInstance();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.CLEARING_PLAYER_INSTANCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PLAYER_INSTANCE_CLEARED;
    }
}
