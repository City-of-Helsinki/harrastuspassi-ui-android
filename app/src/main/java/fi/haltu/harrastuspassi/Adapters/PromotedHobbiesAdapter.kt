package fi.haltu.harrastuspassi.Adapters

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import fi.haltu.harrastuspassi.Activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.Model.Hobby
import fi.haltu.harrastuspassi.R

class PromotedHobbiesAdapter(
    private val context: Context,
    private val activity: FragmentActivity,
    private val hobbiesList: ArrayList<Hobby>
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return hobbiesList.size
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.adapter_promoted_hobby, null)

        val image = view.findViewById<View>(R.id.imageview) as ImageView
        val description = view.findViewById<TextView>(R.id.description)
        image.setImageResource(hobbiesList[position].image)
        description.text = hobbiesList[position].title
        val viewPager = container as ViewPager
        viewPager.addView(view, 0)

        view.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HobbyDetailActivity::class.java)

            val sharedView: View = image
            val transition = activity.getString(R.string.item_detail)

            intent.putExtra("EXTRA_HOBBY", hobbiesList[position])
            val transitionActivity = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, transition)
            activity.startActivity(intent, transitionActivity.toBundle())
        })

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}