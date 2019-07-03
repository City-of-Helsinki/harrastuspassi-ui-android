package fi.haltu.harrastuspassi.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import fi.haltu.harrastuspassi.Activities.MainActivity
import fi.haltu.harrastuspassi.R


class OptionsAdapter(
    private val context: Context,
    private val viewPager: ViewPager,
    private val options: List<String>
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return options.size
    }

    override fun getItem(position: Int): Any {
        return options[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.adapter_list_item_option, parent, false)
        var optionButton = rowView.findViewById<Button>(R.id.optionButton)
        optionButton.text = options[position]
        optionButton.setOnClickListener {
            if (viewPager.adapter?.count == viewPager.currentItem.plus(1)) {
                Log.d("Last", "Last child")
                val intent = Intent(context, MainActivity::class.java)
                val activity = context as Activity
                activity.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)

            } else {
                viewPager.currentItem = viewPager.currentItem + 1
            }
            optionButton.setBackgroundColor(Color.GRAY)
        }
        return rowView
    }
}