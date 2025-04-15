package cn.androidx.ijkplayer.controller

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import cn.androidx.ijkplayer.R
import cn.androidx.ijkplayer.databinding.IjkControllerTopBootomBinding
import cn.androidx.ijkplayer.utils.IjkUtils
import cn.androidx.ijkplayer.view.IJKPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import kotlin.math.absoluteValue


/**
 * by JFZ
 * 2025/4/12
 * desc：顶部、底部 控制布局
 **/
class TopBottomBarController : ControllerCase {

    companion object {
        @JvmStatic
        val ID = "TopBottomBarController".hashCode().absoluteValue
    }

    private lateinit var player: IJKPlayer

    private lateinit var mediaPlayer: IjkMediaPlayer
    private var isShowBar = false
    private val barHandler = Handler(Looper.getMainLooper())
    private val timeHandler = Handler(Looper.getMainLooper())

    private val controllerView: FrameLayout by lazy {
        player.findViewById(R.id.player_controller)
    }

    private val binding: IjkControllerTopBootomBinding by lazy {
        IjkControllerTopBootomBinding.inflate(LayoutInflater.from(player.context))
    }

    //是否显示top bar
    private var enableTopBar = true

    //是否显示bottom bar
    private var enableBottomBar = true

    private val barDuration = 3000L

    private val uiChangedListeners = mutableListOf<OnUIChangedListener>()

    override fun onCreate(parentView: IJKPlayer, mp: IjkMediaPlayer) {
        this.player = parentView
        this.mediaPlayer = mp
        controllerView.removeView(binding.root)
        controllerView.addView(
            binding.root,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        //空实现
        binding.topLl.setOnClickListener {
            //not nothing
        }
        binding.bottomLl.setOnClickListener {
            //not nothing
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                barHandler.removeCallbacks(barRun)
                barHandler.removeCallbacksAndMessages(null)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                barHandler.postDelayed(barRun, barDuration)
                try {
                    val progress = seekBar.progress.toFloat() / 100f
                    mediaPlayer.seekTo((mediaPlayer.duration * progress).toLong())
                } catch (e: Exception) {
                }
            }
        })

        binding.back.setOnClickListener { _ ->
            IjkUtils.getActivity(player.context)?.onBackPressed()
        }

        binding.playState.setOnClickListener {
            if (!player.isVideoPrepared() || player.isVideoComplete() || player.isVideoStop()) {
                player.starAfterPrepared()
            } else {
                if (player.isVideoPause()) {
                    player.play()
                } else {
                    player.pause()
                }
            }
        }

        binding.screen.setOnClickListener {
            player.autoLandAndPortrait()
        }
    }

    override fun onFullScreen() {
        binding.screen.setImageResource(R.drawable.ijk_fullscreen_exit_white_24)
        postBar()
    }

    override fun onExitFullScreen() {
        postBar()
        binding.screen.setImageResource(R.drawable.ijk_fullscreen_white_24)
    }

    override fun onSingleTapConfirmed(e: MotionEvent) {
        if (!player.isVideoPrepared()) return
        if (player.isVideoComplete()) return
        if (player.isVideoStop()) return
        updateBarUI()
    }

    private val barRun = {
        if (!isShowBar) isShowBar = true
        showOrHideBar()
    }

    private fun updateBarUI() {
        showOrHideBar()
        postBar()
    }

    private fun postBar() {
        barHandler.removeCallbacks(barRun)
        barHandler.removeCallbacksAndMessages(null)
        barHandler.postDelayed(barRun, barDuration)
    }

    private fun showOrHideBar() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.cl)
        val transition = AutoTransition().apply {
            this.duration = 200
        }

        if (!isShowBar) {
            isShowBar = true
            if (enableTopBar) {
                constraintSet.clear(R.id.top_ll, ConstraintSet.BOTTOM)
                constraintSet.connect(
                    R.id.top_ll,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                uiChangedListeners.forEach { it.onTopBarChanged(true) }
            }
            if (enableBottomBar) {
                constraintSet.clear(R.id.bottom_ll, ConstraintSet.TOP)
                constraintSet.connect(
                    R.id.bottom_ll,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                uiChangedListeners.forEach { it.onBottomBarChanged(true) }
            }
            constraintSet.setVisibility(R.id.play_state, View.VISIBLE)
        } else {
            isShowBar = false
            if (enableTopBar) {
                constraintSet.clear(R.id.top_ll, ConstraintSet.TOP)
                constraintSet.connect(
                    R.id.top_ll,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                uiChangedListeners.forEach { it.onTopBarChanged(false) }
            }
            if (enableBottomBar) {
                constraintSet.clear(R.id.bottom_ll, ConstraintSet.BOTTOM)
                constraintSet.connect(
                    R.id.bottom_ll,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                uiChangedListeners.forEach { it.onBottomBarChanged(false) }
            }
            constraintSet.setVisibility(
                R.id.play_state,
                if (!player.isVideoPrepared() || player.isVideoComplete() || player.isVideoStop()) View.VISIBLE else
                    View.GONE
            )

        }

        TransitionManager.beginDelayedTransition(binding.cl, transition)
        constraintSet.applyTo(binding.cl)
    }

    override fun onCompletion(mp: IMediaPlayer) {
        isShowBar = true
        showOrHideBar()
        updateTimeUI()
        updatePlayUI()
    }

    override fun onDoubleTap(e: MotionEvent) {
        if (player.isVideoPrepared()) {
            if (player.isVideoPause()) {
                player.play()
            } else {
                player.pause()
            }
        }
    }

    override fun onVideoPause() {
        updatePlayUI()
    }

    override fun onVideoPlay() {
        updatePlayUI()
        updateBarUI()
    }

    override fun onVideoStop() {
        isShowBar = true
        updateBarUI()
        updatePlayUI()
        updateTime()
    }

    override fun onPrepared(mp: IMediaPlayer) {
        updateTime(0)
        updatePlayUI()
        updateBarUI()
    }

    override fun onVideoStart() {
        updateTime()
    }

    private fun updatePlayUI() {
        if (!player.isVideoPrepared() || player.isVideoComplete() || player.isVideoPause() || player.isVideoStop()) {
            binding.playState.setImageResource(R.drawable.ijk_play_white_24)
        } else {
            binding.playState.setImageResource(R.drawable.ijk_pause_white_24)
        }
    }

    private val timeDurationRun = {
        if (mediaPlayer.isPlaying) {
            updateTimeUI()
            updateTime()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeUI() {
        val timeStr =
            DateUtils.formatElapsedTime((mediaPlayer.duration - mediaPlayer.currentPosition) / 1000)
        binding.duration.text = timeStr
        binding.seekBar.progress =
            (mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat() * 100).toInt()
    }

    private fun updateTime(duration: Long = 1000) {
        timeHandler.removeCallbacksAndMessages(null)
        timeHandler.postDelayed(timeDurationRun, duration)
    }

    override fun onSeekComplete(mp: IMediaPlayer) {
        super.onSeekComplete(mp)
        updateTime()
        if (player.isVideoComplete() && !mediaPlayer.isPlaying) {
            player.setVideoComplete(false)
            player.play()
        }
        if (player.isVideoPause()) {
            player.play()
        }
        updatePlayUI()
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
        isLeft: Boolean,
        isRight: Boolean,
        isUp: Boolean,
        isDown: Boolean
    ) {
        isShowBar = true
        showOrHideBar()
    }

    override fun onScaleBegin(detector: ScaleGestureDetector) {
        isShowBar = true
        showOrHideBar()
    }

    override fun onResume() {
        updateTime()
    }

    override fun onPause() {
        timeHandler.removeCallbacks(timeDurationRun)
        timeHandler.removeCallbacksAndMessages(null)
    }

    override fun reset() {
        barHandler.removeCallbacks(barRun)
        barHandler.removeCallbacksAndMessages(null)

        timeHandler.removeCallbacks(timeDurationRun)
        timeHandler.removeCallbacksAndMessages(null)

        isShowBar = false
    }

    override fun getId() = TopBottomBarController.ID

    fun setEnableTopBar(enable: Boolean) {
        this.enableTopBar = enable
    }

    fun setEnableBottomBar(enable: Boolean) {
        this.enableBottomBar = enable
    }

    fun addOnUIChangedListener(listener: OnUIChangedListener) {
        uiChangedListeners.add(listener)
    }

    fun removeOnUIChangedListener(listener: OnUIChangedListener) {
        uiChangedListeners.remove(listener)
    }

    override fun onDestroy() {
        uiChangedListeners.clear()
        reset()
    }

    interface OnUIChangedListener {
        fun onTopBarChanged(isShow: Boolean)
        fun onBottomBarChanged(isShow: Boolean)

    }

}