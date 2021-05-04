package fi.haltu.harrastuspassi.utils.splashScreenUtil

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.annotation.DrawableRes


/**
 * This class implements View -class and sets drawable component that
 * covers whole screen except status bar and system navigation.
 *
 * @param context context where this view should place
 * @property splashDrawable drawable component that covers whole screen
 */
class SplashView(context: Context) : View(context) {
    var splashDrawable: Drawable? = null

    fun setSplashDrawable(@DrawableRes drawable: Int) {
        splashDrawable = ContextCompat.getDrawable(context, drawable)
        splashDrawable!!.callback = this
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val leftBound: Int
        val topBound: Int
        val rightBound: Int
        val bottomBound: Int

        /*
         * From SDK version 23, user is able to split screen to use multiple applications.
         * SDK version should be 23 to use rootWindowInsets -function to get size of
         * application view.
         */
        if(VERSION.SDK_INT >= VERSION_CODES.M) {
            val windowInsets = rootWindowInsets
            leftBound = -windowInsets.systemWindowInsetLeft
            // Height of status bar.
            topBound = -windowInsets.systemWindowInsetTop
            rightBound = width + windowInsets.systemWindowInsetRight
            bottomBound = height + windowInsets.systemWindowInsetBottom
        } else {
            val activity = context as Activity
            val window = activity.window
            val rectangle = Rect()
            // decorView is view from bottom of status bar to top of system navigation.
            window.decorView.getWindowVisibleDisplayFrame(rectangle)
            leftBound = -rectangle.left
            topBound = -rectangle.top
            rightBound= rectangle.right - rectangle.left
            bottomBound = rectangle.bottom - rectangle.top
        }

        splashDrawable!!.setBounds(
             leftBound,
             topBound,
             rightBound,
             bottomBound
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        splashDrawable!!.draw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === splashDrawable || super.verifyDrawable(who)
    }

}