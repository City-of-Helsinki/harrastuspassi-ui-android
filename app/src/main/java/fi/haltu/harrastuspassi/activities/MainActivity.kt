package fi.haltu.harrastuspassi.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fi.haltu.harrastuspassi.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }
}
