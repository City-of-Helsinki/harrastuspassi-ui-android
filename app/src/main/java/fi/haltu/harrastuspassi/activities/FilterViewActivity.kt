package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import fi.haltu.harrastuspassi.R

class FilterViewActivity : AppCompatActivity() {

    var hobbyTestResult = arrayOf("Jalkapallo","Suunnistus", "Piirtäminen")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_view)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator (R.drawable.ic_clear_black_24dp)
        supportActionBar!!.title = "Suodata"

    }

    fun openCategories (view: View) {
        val intent = Intent(this, HobbyCategoriesActivity::class.java).apply {

        }
        startActivity(intent)
    }
}
