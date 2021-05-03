package fi.haltu.harrastuspassi.utils.splashScreenUtil

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

fun circularHide(view: View, duration: Long) {
    if(SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        view.post {
            val width = view.width
            val height = view.height
            val centerX = width / 2
            val centerY = height / 2
            val radius = width.coerceAtLeast(height).toFloat()

            val reveal =
                ViewAnimationUtils.createCircularReveal(view, centerX, centerY, radius, 0F)
            reveal.duration = duration
            reveal.addListener(SplashAnimatorListener(view))
            reveal.start()
        }
    }
}

fun fadeIn(view: View, duration: Long) {
    view.alpha = 0f
    view
        .animate()
        .setDuration(duration)
        .alpha(1f)
        .setInterpolator(DecelerateInterpolator())
        .start()
}

fun fadeIn(drawable: Drawable, duration: Long) {
    drawable.alpha = 0
    val animator = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofInt("alpha", 255))
    animator.target = drawable
    animator.duration = duration
    animator.start()
}

fun fadeOut(view: View, duration: Long) {
    view.alpha = 1f
    view
        .animate()
        .setDuration(duration)
        .alpha(0f)
        .setListener(SplashAnimatorListener(view))
        .setInterpolator(DecelerateInterpolator())
        .start()
}

fun fadeOutInOrder(view: View, logo: Drawable, duration: Long) {
    logo.alpha = 1
    val animator = ObjectAnimator.ofPropertyValuesHolder(logo, PropertyValuesHolder.ofInt("alpha", 0))
    animator.target = logo
    animator.duration = duration
    animator.addListener(object: AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            fadeOut(view, duration)
        }
        override fun onAnimationCancel(animation: Animator?) {
            fadeOut(view, duration)
        }
    })
    animator.start()
}

fun hideLeft(view: View, screenWidth: Float, duration: Long) {
    val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, screenWidth)
    animator.duration = duration
    animator.addListener(SplashAnimatorListener(view))
    animator.start()
}

fun hideRight(view: View, screenWidth: Float, duration: Long) {
    val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, -screenWidth)
    animator.duration = duration
    animator.addListener(SplashAnimatorListener(view))
    animator.start()
}

fun hideDown(view: View, screenHeight: Float, duration: Long) {
    val animator = ObjectAnimator.ofFloat(view, "translationY", 0f, screenHeight)
    animator.duration = duration
    animator.addListener(SplashAnimatorListener(view))
    animator.start()
}

fun hideUp(view: View, screenHeight: Float, duration: Long) {
    val animator = ObjectAnimator.ofFloat(view, "translationY", 0f, -screenHeight)
    animator.duration = duration
    animator.addListener(SplashAnimatorListener(view))
    animator.start()
}

class SplashAnimatorListener(private val view: View): AnimatorListenerAdapter() {
    override fun onAnimationEnd(animation: Animator?) {
        removeView(view)
    }
    override fun onAnimationCancel(animation: Animator?) {
        removeView(view)
    }
}

fun removeView(view: View) {
    val parent: ViewGroup? = view.parent as ViewGroup
    parent?.removeView(view)
}