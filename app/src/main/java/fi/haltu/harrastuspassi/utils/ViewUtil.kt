package fi.haltu.harrastuspassi.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Patterns
import android.view.View
import android.widget.TextView

/**
 * Enables click support for a TextView from a [fullText] String, which one containing one or multiple URLs.
 * The [callback] will be called when a click is triggered.
 */
fun TextView.setTextWithLinkSupport(
    fullText: String,
    callback: (String) -> Unit
) {
    val spannable = SpannableString(fullText)
    val matcher = Patterns.WEB_URL.matcher(spannable)
    while (matcher.find()) {
        val url = spannable.toString().substring(matcher.start(), matcher.end())
        val urlSpan = object : URLSpan(fullText) {
            override fun onClick(widget: View) {
                callback(url)
            }
        }
        spannable.setSpan(urlSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = spannable
    movementMethod = LinkMovementMethod.getInstance() // Make link clickable
}