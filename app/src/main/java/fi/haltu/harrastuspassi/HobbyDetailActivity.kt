package fi.haltu.harrastuspassi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class HobbyDetailActivity : AppCompatActivity() {
    private lateinit var hobbyImage: ImageView
    private lateinit var hobbyTitle: TextView
    private lateinit var organizer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)

        val hobby = intent.extras.getSerializable("EXTRA_HOBBY") as Hobby
        title = (hobby.title)

        hobbyImage = findViewById(R.id.hobby_image)
        hobbyImage.setImageResource(hobby.image)

        hobbyTitle = findViewById(R.id.hobby_title)
        hobbyTitle.text = hobby.title

        organizer = findViewById(R.id.hobby_organizer)
        organizer.text = hobby.organizer
    }
}
