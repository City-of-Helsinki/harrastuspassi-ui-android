package fi.haltu.harrastuspassi.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import fi.haltu.harrastuspassi.Model.Inquiry
import fi.haltu.harrastuspassi.R


class WizardSliderAdapter(private val context: Context,
                          private val inquirys: ArrayList<Inquiry>) : PagerAdapter() {

    private var layoutInflater : LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return inquirys.size
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.adapter_wizard_item, null)

        val textView = view.findViewById(R.id.textView) as TextView
        val linearLayout = view.findViewById(R.id.wizard_linear_layout) as LinearLayout
        val viewPager = container as ViewPager
        val optionList = view.findViewById<ListView>(R.id.optionList)
        val adapter = OptionsAdapter(context, viewPager, inquirys!![position].options)
        optionList.adapter = adapter

        textView.text = inquirys!![position].question
        linearLayout.setBackgroundColor(inquirys!![position].color)

        viewPager.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}