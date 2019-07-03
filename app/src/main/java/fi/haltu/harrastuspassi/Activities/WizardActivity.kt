package fi.haltu.harrastuspassi.Activities

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Button
import fi.haltu.harrastuspassi.Model.Inquiry
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.Adapters.WizardSliderAdapter

class WizardActivity : AppCompatActivity() {
    private lateinit var viewpager : ViewPager
    private lateinit var skipButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        skipButton = findViewById(R.id.skip_button)
        skipButton.setOnClickListener {
            finish()
        }
        viewpager = findViewById(R.id.wizard_viewPager)
        val adapter = WizardSliderAdapter(this, addInquiry())
        val indicator = findViewById<TabLayout>(R.id.indicator)
        viewpager.adapter = adapter
        indicator.setupWithViewPager(viewpager, true)

    }

    private fun addInquiry() : ArrayList<Inquiry> {
        var inquiries = ArrayList<Inquiry>()
        var inquiry = Inquiry()
        inquiry.question = "Mitä haluaisit kokeilla?"
        inquiry.options = listOf<String>("Lukemista","Rullaluistelua", "Tähtitiedettä", "Jotain muuta...")
        inquiry.color = Color.RED
        inquiries.add(inquiry)

        inquiry = Inquiry()
        inquiry.question = "Koska sinulla olisi aikaa?"
        inquiry.options = listOf<String>("Nyt","Huomenna", "Ylihuomenna", "Myöhemmin")
        inquiry.color = Color.DKGRAY
        inquiries.add(inquiry)

        inquiry = Inquiry()
        inquiry.question = "Kuinka kaukana?"
        inquiry.options = listOf<String>("Kävellen","Pyörällä", "Bussilla", "Muualla...")
        inquiry.color = Color.BLUE
        inquiries.add(inquiry)

        return inquiries
    }
}
