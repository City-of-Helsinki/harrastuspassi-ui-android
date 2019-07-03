package fi.haltu.harrastuspassi.Adapters

import android.content.Context
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import fi.haltu.harrastuspassi.R

class OptionsAdapter(
    private val context: Context,
    private val viewPager: ViewPager,
    private val options: List<String>
    ) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
        // Get view for row item
        val rowView = inflater.inflate(R.layout.options_list_item, parent, false)
        var optionButton = rowView.findViewById<Button>(R.id.optionButton)
        optionButton.text = options[position]
        optionButton.setOnClickListener {
            if(viewPager.adapter?.count == viewPager.currentItem.plus(1)) {
                Log.d("Last", "Last child")

            } else {
                viewPager.currentItem = viewPager.currentItem + 1

            }
            optionButton.setBackgroundColor(Color.GRAY)
        }
        return rowView
    }

}