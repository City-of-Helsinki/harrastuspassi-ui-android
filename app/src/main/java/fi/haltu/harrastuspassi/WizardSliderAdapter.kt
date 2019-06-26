package fi.haltu.harrastuspassi

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView


class WizardSliderAdapter(private val context: Context,
                          private val color: ArrayList<Int>,
                          private val colorName: ArrayList<String>) : PagerAdapter() {

    private var layoutInflater : LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return color.size
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.wizard_item_slider , null)

        val textView = view.findViewById(R.id.textView) as TextView
        val linearLayout = view.findViewById(R.id.wizard_linear_layout) as LinearLayout
        val viewPager = container as ViewPager

        textView.text = colorName!![position]
        linearLayout.setBackgroundColor(color!![position])

        viewPager.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}