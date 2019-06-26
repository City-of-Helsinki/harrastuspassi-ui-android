package fi.haltu.harrastuspassi

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Button

class WizardActivity : AppCompatActivity() {
    private lateinit var viewpager : ViewPager
    private var color: ArrayList<Int>? = null
    private var colorName: ArrayList<String>? = null
    private lateinit var skipButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        color = ArrayList()
        color!!.add(Color.RED)
        color!!.add(Color.GREEN)
        color!!.add(Color.BLUE)

        colorName = ArrayList()
        colorName!!.add("RED")
        colorName!!.add("GREEN")
        colorName!!.add("BLUE")

        skipButton = findViewById(R.id.skip_button)
        skipButton.setOnClickListener {
            finish()
        }
        viewpager = findViewById(R.id.wizard_viewPager)
        val adapter = WizardSliderAdapter(this, color!!, colorName!!)
        val indicator = findViewById<TabLayout>(R.id.indicator)
        viewpager.adapter = adapter
        indicator.setupWithViewPager(viewpager, true)
    }
}
