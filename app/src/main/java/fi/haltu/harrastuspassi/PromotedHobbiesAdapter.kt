package fi.haltu.harrastuspassi

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class PromotedHobbiesAdapter(private val context : Context) : PagerAdapter() {
    private var layoutInflater : LayoutInflater? = null
    val Image = arrayOf(R.drawable.image_1 , R.drawable.image_2 , R.drawable.image_3)


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view ===  `object`
    }

    override fun getCount(): Int {
        return Image.size
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.promoted_hobby_activity , null)

        val image = view.findViewById<View>(R.id.imageview) as ImageView
        image.setImageResource(Image[position])
        val viewPager = container as ViewPager
        viewPager.addView(view , 0)

        return view

    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}