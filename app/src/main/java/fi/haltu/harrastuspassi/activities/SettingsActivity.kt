package fi.haltu.harrastuspassi.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import fi.haltu.harrastuspassi.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.title = "Settings"
    }
}
