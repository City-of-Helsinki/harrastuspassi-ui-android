package fi.haltu.harrastuspassi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.models.Settings

class LocationListAdapter(private var locationList: Settings) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_location_list_item, parent, false)
        return LocationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val location: Location = locationList.locationList[position]
        (holder as LocationListViewHolder).bind(location, position)
    }

    override fun getItemCount(): Int = locationList.locationList.size

    inner class LocationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var radioButton: RadioButton = itemView.findViewById(R.id.radio_button)
        var cityTextView:TextView = itemView.findViewById(R.id.city_name)
        var addressTextView: TextView = itemView.findViewById(R.id.address)

        fun bind(location: Location, position: Int) {
            cityTextView.text = location.city!!.toUpperCase()
            addressTextView.text = location.address

            radioButton.setOnClickListener {
                itemCheckChanged(position)
            }

            cityTextView.setOnClickListener {
                radioButton.performClick()
                itemCheckChanged(position)
            }

            addressTextView.setOnClickListener {
                radioButton.performClick()
                itemCheckChanged(position)
            }

            radioButton.isChecked = locationList.selectedIndex == position

            radioButton.isEnabled = !locationList.useCurrentLocation
            cityTextView.isEnabled = !locationList.useCurrentLocation
            addressTextView.isEnabled = !locationList.useCurrentLocation
        }

        private fun itemCheckChanged(position: Int) {
            val previousIndex = locationList.selectedIndex
            locationList.selectedIndex = position
            notifyItemChanged(previousIndex)
            notifyItemChanged(position)
        }
    }
}