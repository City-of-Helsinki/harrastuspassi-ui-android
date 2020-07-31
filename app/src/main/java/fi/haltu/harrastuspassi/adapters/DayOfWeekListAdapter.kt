package fi.haltu.harrastuspassi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.FilterViewActivity


class DayOfWeekListAdapter(
    private val list: HashSet<Int>,
    private val activity: FilterViewActivity,
    private val clickListener: (Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dayOfWeekList: Map<Int, String> = createDayOfWeekList(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayOfWeekViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_day_of_week_list_item, parent, false)
        return DayOfWeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DayOfWeekViewHolder).bind(position + 1, clickListener)
    }

    override fun getItemCount(): Int = dayOfWeekList.size

    private fun createDayOfWeekList(activity: FilterViewActivity): Map<Int, String> {
        return mapOf(
            1 to activity.getString(R.string.monday),
            2 to activity.getString(R.string.tuesday),
            3 to activity.getString(R.string.wednesday),
            4 to activity.getString(R.string.thursday),
            5 to activity.getString(R.string.friday),
            6 to activity.getString(R.string.saturday),
            7 to activity.getString(R.string.sunday)
        )
    }

    inner class DayOfWeekViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var dayButton: TextView = itemView.findViewById(R.id.day_text)

        fun bind(position: Int, clickListener: (Int) -> Unit) {
            val dayOfWeek: String = dayOfWeekList[position].toString()
            dayButton.text = dayOfWeek
            if (list.contains(position)) {
                dayButton.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.hobbyPurpleLight
                    )
                )
                dayButton.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

            } else {
                dayButton.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                dayButton.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.hobbyBlue
                    )
                )

            }

            itemView.setOnClickListener { clickListener(position) }
        }
    }
}