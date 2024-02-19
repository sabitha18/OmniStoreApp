package com.armada.storeapp.ui.utils.video_view.player_messages;

import com.armada.storeapp.ui.utils.video_view.PlayerMessageState;
import com.armada.storeapp.ui.utils.video_view.VideoPlayerManagerCallback;
import com.armada.storeapp.ui.utils.video_view.VideoPlayerView;

/**
 * This is generic PlayerMessage for setDataSource
 */
public abstract class SetDataSourceMessage extends PlayerMessage {

    public SetDataSourceMessage(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_DATA_SOURCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.DATA_SOURCE_SET;
    }
}
