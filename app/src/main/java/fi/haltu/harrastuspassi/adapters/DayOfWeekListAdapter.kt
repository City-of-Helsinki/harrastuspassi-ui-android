package fi.haltu.harrastuspassi.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import fi.haltu.harrastuspassi.R


class DayOfWeekListAdapter(private val list: HashSet<Int>, private val clickListener: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dayOfWeekList: Map<Int, String> = createDayOfWeekList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayOfWeekViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_day_of_week_list_item, parent, false)
        return DayOfWeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DayOfWeekViewHolder).bind(position + 1, clickListener)
    }

    override fun getItemCount(): Int = dayOfWeekList.size

    private fun createDayOfWeekList(): Map<Int, String> {
        //TODO there should be translation later
        return mapOf(1 to "Maanantai", 2 to "Tiistai", 3 to "Keskiviikko", 4 to "Torstai",
            5 to "Perjantai", 6 to "Lauantai", 7 to "Sunnuntai")
    }

    inner class DayOfWeekViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var dayButton: TextView = itemView.findViewById(R.id.day_text)

        fun bind(position: Int, clickListener: (Int) -> Unit) {
            val dayOfWeek: String = dayOfWeekList[position].toString()
            Toast.makeText(itemView.context, "data changed", Toast.LENGTH_SHORT).show()
            dayButton.text = dayOfWeek
            if(list.contains(position)){
                dayButton.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.hobbyPurpleLight))
            } else {
                dayButton.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            }

            itemView.setOnClickListener {clickListener(position)}
        }
    }
}