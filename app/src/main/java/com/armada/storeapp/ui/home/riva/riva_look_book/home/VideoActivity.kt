package com.armada.riva.HOME

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import com.armada.storeapp.databinding.ActivityVideoBinding
import com.armada.storeapp.ui.base.BaseActivity


/**
 * Created by User999 on 7/11/2018.
 */
class VideoActivity : BaseActivity() {
    private var binding: ActivityVideoBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /* video_player.setUp(intent.getStringExtra("video_path"), JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, intent.getStringExtra("name"))
         video_player.startButton.performClick()


         video_player.backButton.visibility=View.GONE
         video_player.batteryLevel.visibility=View.GONE*/

        binding?.imgCross?.setColorFilter(Color.parseColor("#FFFFFF"))

        if (intent.hasExtra("video_path") && !intent.getStringExtra("video_path").isNullOrEmpty()) {
//            binding?..setSource(Uri.parse(intent.getStringExtra("video_path"))!!)
//            video_player.setAutoPlay(true)
//            video_player.setHideControlsOnPlay(false)
        }
        binding?.relClose?.setOnClickListener {

            /* video_player.cancelDismissControlViewTimer()
             if (JZVideoPlayer.backPress()) {
                 true
             }*/
            // JZVideoPlayer.releaseAllVideos()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        /*  if (JZVideoPlayer.backPress()) {
              true
          }*/
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        // JZVideoPlayer.releaseAllVideos()
//        video_player.pause()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}