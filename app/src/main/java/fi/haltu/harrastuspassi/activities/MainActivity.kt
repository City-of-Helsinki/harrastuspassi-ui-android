package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import fi.haltu.harrastuspassi.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val TAG = "onCreate"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayShowCustomEnabled(true)

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                Log.d(TAG, "resolving deeplink")

                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d(TAG, "deeplink: ${deepLink.toString()}")
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
   }
}




