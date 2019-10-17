package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.fragments.FavoriteListFragment
import fi.haltu.harrastuspassi.fragments.HobbyEventListFragment
import fi.haltu.harrastuspassi.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: ActionBar
    lateinit var hobbyEventListFragment: Fragment
    lateinit var favoriteListFragment: Fragment
    lateinit var settingsFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        val TAG = "onCreate"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        //BOTTOM NAVIGATION BAR
        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener (onNavigationItemSelectedListener)
        hobbyEventListFragment = HobbyEventListFragment()
        favoriteListFragment = FavoriteListFragment()
        settingsFragment = SettingsFragment()
        openFragment(hobbyEventListFragment)

        //FIREBASE_DYNAMIC_LINK
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                Log.d(TAG, "resolving deeplink")

                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    val deepLinkStr = deepLink.toString()
                    when {
                        //works only if deep links form is: https://hpassi.page.link/share/?hobbyEvent={HOBBY_ID}
                        deepLinkStr.contains("?hobbyEvent") -> {
                            val hobbyID: Int = deepLinkStr.substringAfter("?hobbyEvent=").toInt()
                            Log.d(TAG, "hobbyID: $hobbyID")
                            val intent = Intent(this, HobbyDetailActivity::class.java)

                            intent.putExtra("EXTRA_HOBBY_ID",hobbyID)
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

                            startActivity(intent)
                        }
                    }
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId) {
            R.id.navigation_list -> {
                openFragment(hobbyEventListFragment)
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_favorites -> {
                openFragment(favoriteListFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                openFragment(settingsFragment)
                return@OnNavigationItemSelectedListener true
            }
        }

        false

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.bottom_navigation_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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




