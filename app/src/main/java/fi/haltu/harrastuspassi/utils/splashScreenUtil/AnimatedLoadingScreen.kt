package fi.haltu.harrastuspassi.utils.splashScreenUtil

import android.app.Activity
import android.graphics.Point
import android.graphics.drawable.LayerDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import fi.haltu.harrastuspassi.R


class AnimatedLoadingScreen {
    companion object {
        private val DEFAULT_HIDE_ANIMATION = HideAnimationType.FADE_OUT
        private const val DEFAULT_DURATION: Long = 500
        private var screenWidth = 0f
        private var screenHeight = 0f
        private var hideAnimationType: HideAnimationType = DEFAULT_HIDE_ANIMATION
        private var drawableResId = -1
        private var hideAnimationDuration: Long = DEFAULT_DURATION
        var isVisible = false

        fun setHideAnimation(animationType: HideAnimationType) {
            setHideAnimation(animationType, DEFAULT_DURATION)
        }

        fun setHideAnimation(animationType: HideAnimationType, duration: Long) {
            this.hideAnimationType = animationType
            hideAnimationDuration = duration
        }

        fun show(drawableResId: Int, parentActivity: Activity) {
            val splashView = SplashView(parentActivity)
            isVisible = true
            this.drawableResId = drawableResId
            splashView.setSplashDrawable(drawableResId)
            splashView.id = drawableResId
            val layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            val window: Window = parentActivity.window
            val point = Point()
            window.windowManager.defaultDisplay.getSize(point)
            screenWidth = point.x.toFloat()
            screenHeight = point.y.toFloat()
            window.addContentView(splashView, layoutParams)
        }

        fun hide(activity: Activity) {
            if(isVisible) {
                val splashView: SplashView = activity.findViewById(drawableResId)
                val logoIcon = (splashView.splashDrawable as LayerDrawable).findDrawableByLayerId(R.id.logo)

                when(hideAnimationType) {
                    HideAnimationType.NO_ANIMATION -> {
                        removeView(splashView)
                    }
                    HideAnimationType.FADE_OUT -> {
                        fadeOut(splashView, hideAnimationDuration)
                    }
                    HideAnimationType.FADE_OUT_IN_ORDER -> {
                        fadeOutInOrder(splashView, logoIcon, hideAnimationDuration)
                    }
                    HideAnimationType.HIDE_LEFT -> {
                        hideRight(splashView, screenWidth, hideAnimationDuration)
                    }
                    HideAnimationType.HIDE_RIGHT -> {
                        hideLeft(splashView, screenWidth, hideAnimationDuration)
                    }
                    HideAnimationType.HIDE_UP -> {
                        hideUp(splashView, screenHeight, hideAnimationDuration)
                    }
                    HideAnimationType.HIDE_DOWN -> {
                        hideDown(splashView, screenHeight, hideAnimationDuration)
                    }
                    HideAnimationType.CIRCULAR_HIDE -> {
                        // Sdk version must be 21+ to use this animation
                        if(SDK_INT >= VERSION_CODES.LOLLIPOP) {
                            circularHide(splashView, hideAnimationDuration)
                        } else {
                            fadeOut(splashView, DEFAULT_DURATION)
                        }
                    }
                }
            }
        }
    }
}