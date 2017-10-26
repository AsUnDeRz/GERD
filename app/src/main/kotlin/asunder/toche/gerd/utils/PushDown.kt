package asunder.toche.gerd

import android.animation.*
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator


/**
 * Created by ToCHe on 10/23/2017 AD.
 */
class PushDown private constructor(private val view: View?) {

    private val defaultScale: Float
    private var scale = DEFAULT_PUSH_SCALE
    private var durationPush = DEFAULT_PUSH_DURATION
    private var durationRelease = DEFAULT_RELEASE_DURATION
    private var interpolatorPush = DEFAULT_INTERPOLATOR
    private var interpolatorRelease = DEFAULT_INTERPOLATOR

    init {
        defaultScale = view!!.scaleX
    }

    fun setScale(scale: Float): PushDown {
        this.scale = scale
        return this
    }

    fun setDurationPush(duration: Long): PushDown {
        this.durationPush = duration
        return this
    }

    fun setDurationRelease(duration: Long): PushDown {
        this.durationRelease = duration
        return this
    }

    fun setInterpolatorPush(interpolatorPush: AccelerateDecelerateInterpolator): PushDown {
        this.interpolatorPush = interpolatorPush
        return this
    }

    fun setInterpolatorRelease(interpolatorRelease: AccelerateDecelerateInterpolator): PushDown {
        this.interpolatorRelease = interpolatorRelease
        return this
    }


    fun setOnClickListener(clickListener: View.OnClickListener?): PushDown {
        if (view != null) {
            view!!.setOnClickListener(clickListener)
        }
        return this
    }

    private fun setOnTouchEvent(eventListener: View.OnTouchListener?): PushDown {
        if (view != null) {
            view!!.setOnTouchListener(object : View.OnTouchListener {
                var isOutSide: Boolean = false
                var rect: Rect? = null

                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                    val i = motionEvent.action
                    if (i == MotionEvent.ACTION_DOWN) {
                        setViewAnimate(view, false)
                        isOutSide = false
                        rect = Rect(
                                view.getLeft(),
                                view.getTop(),
                                view.getRight(),
                                view.getBottom())
                        animScale(view,
                                scale,
                                durationPush,
                                interpolatorPush)
                    } else if (i == MotionEvent.ACTION_MOVE) {
                        if (rect != null
                                && !isOutSide
                                && !rect!!.contains(
                                view.getLeft() + motionEvent.x.toInt(),
                                view.getTop() + motionEvent.y.toInt())) {
                            isOutSide = true
                            animScale(view,
                                    defaultScale,
                                    durationRelease,
                                    interpolatorRelease)
                        }
                    } else if (i == MotionEvent.ACTION_CANCEL) {
                        animScale(view,
                                defaultScale,
                                durationRelease,
                                interpolatorRelease)
                    } else if (i == MotionEvent.ACTION_UP) {
                        if (!isOutSide) {
                            setViewAnimate(view, false)
                        }
                        animScale(view,
                                defaultScale,
                                durationRelease,
                                interpolatorRelease)
                    }
                    return if (eventListener != null) {
                        eventListener!!.onTouch(view, motionEvent)
                    } else {
                        false
                    }
                }
            })
        }

        return this
    }

    private fun animScale(view: View,
                          scale: Float,
                          duration: Long,
                          interpolator: TimeInterpolator) {

        if (!isViewAnimate(view)) {
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale)
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale)
            scaleX.interpolator = interpolator
            scaleX.duration = duration
            scaleY.interpolator = interpolator
            scaleY.duration = duration

            val scaleAnimSet = AnimatorSet()
            scaleAnimSet
                    .play(scaleX)
                    .with(scaleY)
            scaleX.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    setViewAnimate(view, true)
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    setViewAnimate(view, false)
                }
            })
            scaleX.addUpdateListener {
                val p = view.parent as View
                p.invalidate()
            }
            scaleAnimSet.start()
        }
    }

    private fun isViewAnimate(view: View): Boolean {
        if (view.getTag(R.string.tag_anim_state) == null) {
            view.setTag(R.string.tag_anim_state, false)
            return false
        }

        return view.getTag(R.string.tag_anim_state) as Boolean
    }

    private fun setViewAnimate(view: View, isAnimate: Boolean) {
        view.setTag(R.string.tag_anim_state, isAnimate)
    }

    companion object {
        val DEFAULT_PUSH_SCALE = 0.97f
        val DEFAULT_PUSH_DURATION: Long = 50
        val DEFAULT_RELEASE_DURATION: Long = 125
        val DEFAULT_INTERPOLATOR = AccelerateDecelerateInterpolator()

        fun setOnTouchPushDown(view: View,
                                   eventListener: View.OnTouchListener): PushDown {
            val pushAnim = PushDown(view)
            pushAnim.setOnTouchEvent(eventListener)
                    .setOnClickListener(null)
            return pushAnim
        }

        fun setOnTouchPushDown(view: View): PushDown {
            val pushAnim = PushDown(view)
            pushAnim.setOnTouchEvent(null)
                    .setOnClickListener(null)
            return pushAnim
        }
    }

}