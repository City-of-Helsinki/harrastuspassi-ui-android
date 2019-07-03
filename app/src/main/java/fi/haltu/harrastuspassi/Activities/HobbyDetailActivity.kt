package fi.haltu.harrastuspassi.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import fi.haltu.harrastuspassi.Model.Hobby
import fi.haltu.harrastuspassi.R



class HobbyDetailActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var hobbyImage: ImageView
    private lateinit var hobbyTitle: TextView
    private lateinit var hobbyOrganizer: TextView
    private lateinit var dateTime: TextView
    private lateinit var location: TextView
    private lateinit var period: TextView
    private lateinit var description: TextView
    private lateinit var enrolment: TextView
    private lateinit var organizer: TextView
    private lateinit var gDetector: GestureDetectorCompat
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

        this.gDetector = GestureDetectorCompat(this, this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private val swipeThreshhold = 100
    private val swipeVelocityThreshhold = 100

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {

        val diffY = e2!!.y - e1!!.y
        val diffX = e2.x - e1.x
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > swipeThreshhold && Math.abs(velocityX) > swipeVelocityThreshhold) {
                if (diffX > 0) {
                    onBackPressed()
                }
            }
        }
        return true
    }

    override fun onDown(event: MotionEvent): Boolean {
        Log.d("Touch detect","onDown")

        return true
    }

    override fun onLongPress(event: MotionEvent) {
        Log.d("Touch detect","onLongPress")

    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                          distanceX: Float, distanceY: Float): Boolean {
        Log.d("Touch detect","onScroll")

        return true
    }

    override fun onShowPress(event: MotionEvent) {
        Log.d("Touch detect","onShownPress")

    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        Log.d("Touch detect","onSingleTapUp")

        return true
    }
}
