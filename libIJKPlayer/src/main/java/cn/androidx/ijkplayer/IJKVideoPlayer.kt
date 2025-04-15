package cn.androidx.ijkplayer

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import cn.androidx.ijkplayer.controller.ControllerCase
import cn.androidx.ijkplayer.controller.FixFullScreenPauseController
import cn.androidx.ijkplayer.controller.TopBottomBarController
import cn.androidx.ijkplayer.view.IJKPlayer


/**
 * by JFZ
 * 2025/4/11
 * desc：
 **/
class IJKVideoPlayer : IJKPlayer {
    constructor(context: Context) : super(context) {
        setBackgroundColor(Color.BLACK)
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        setBackgroundColor(Color.BLACK)
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        setBackgroundColor(Color.BLACK)
    }

    override fun getControllerCase(): MutableList<ControllerCase> {
        return mutableListOf(TopBottomBarController(), FixFullScreenPauseController())
    }

}