package fi.haltu.harrastuspassi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class HobbyDetailActivity : AppCompatActivity() {
    private lateinit var hobbyImage: ImageView
    private lateinit var hobbyTitle: TextView
    private lateinit var hobbyOrganizer: TextView
    private lateinit var dateTime: TextView
    private lateinit var location: TextView
    private lateinit var period: TextView
    private lateinit var description: TextView
    private lateinit var enrolment: TextView
    private lateinit var organizer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)

        val hobby = intent.extras!!.getSerializable("EXTRA_HOBBY") as Hobby
        title = (hobby.title)

        hobbyImage = findViewById(R.id.hobby_image)
        hobbyTitle = findViewById(R.id.hobby_title)
        hobbyOrganizer = findViewById(R.id.hobby_organizer)
        dateTime = findViewById(R.id.date_time)
        location = findViewById(R.id.location)
        period = findViewById(R.id.period)
        description = findViewById(R.id.description)
        enrolment = findViewById(R.id.enrolment)
        organizer = findViewById(R.id.organizer)

        hobbyImage.setImageResource(hobby.image)
        hobbyTitle.text = hobby.title
        hobbyOrganizer.text = hobby.organizer
        dateTime.text = hobby.duration
        location.text = hobby.place
        period.text = hobby.duration
        description.text = hobby.description
        enrolment.text = hobby.description
        organizer.text = hobby.organizer
    }
}
