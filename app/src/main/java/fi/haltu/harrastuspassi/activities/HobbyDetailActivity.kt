package fi.haltu.harrastuspassi.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.HobbyEvent
import kotlinx.android.synthetic.main.adapter_hobby_event_list_item_hobby.view.*

class HobbyDetailActivity : AppCompatActivity() {

    private lateinit var hobbyImage: ImageView
    private lateinit var hobbyTitle: TextView
    private lateinit var hobbyOrganizer: TextView
    private lateinit var dateTime: TextView
    private lateinit var location: TextView
    private lateinit var description: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)

        val hobby = intent.extras!!.getSerializable("EXTRA_HOBBY") as HobbyEvent
        title = (hobby.title)


        hobbyImage = findViewById(R.id.hobby_image)
        hobbyTitle = findViewById(R.id.hobby_title)
        hobbyOrganizer = findViewById(R.id.hobby_organizer)
        dateTime = findViewById(R.id.date_time)
        location = findViewById(R.id.location)
        description = findViewById(R.id.description_text)

        Picasso.with(this)
            .load(hobby.imageUrl)
            .placeholder(R.drawable.image_placeholder_icon)
            .error(R.drawable.image_placeholder_icon)
            .into(hobbyImage)

        hobbyTitle.text = hobby.title
        hobbyOrganizer.text = hobby.title
        dateTime.text = hobby.dateTime
        location.text = hobby.place
        description.text = hobby.description
    }
}
