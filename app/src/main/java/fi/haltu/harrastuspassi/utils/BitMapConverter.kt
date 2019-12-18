package fi.haltu.harrastuspassi.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    var vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(
        0,
        0,
        vectorDrawable.intrinsicWidth * 2,
        vectorDrawable.intrinsicHeight * 2
    )
    var bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth * 2,
        vectorDrawable.intrinsicHeight * 2,
        Bitmap.Config.ARGB_8888
    )
    var canvas = Canvas(bitmap)

    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}