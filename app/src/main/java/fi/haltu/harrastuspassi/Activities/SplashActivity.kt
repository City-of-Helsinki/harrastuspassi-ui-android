package fi.haltu.harrastuspassi.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.lang.Thread.sleep

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sleep(1000)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
