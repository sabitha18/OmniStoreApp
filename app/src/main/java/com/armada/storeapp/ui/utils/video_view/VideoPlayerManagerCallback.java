package com.armada.storeapp.ui.utils.video_view;

import com.armada.storeapp.ui.utils.MetaData;

/**
 * This callback is used by {@link }
 * to get and set data it needs
 */
public interface VideoPlayerManagerCallback {

    void setCurrentItem(MetaData currentItemMetaData, VideoPlayerView newPlayerView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
