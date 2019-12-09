package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.analytics.FirebaseAnalytics
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.fragments.*
import fi.haltu.harrastuspassi.fragments.home.HomeFragment
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.saveFilters
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: ActionBar
    lateinit var bottomNavigationView: BottomNavigationView
    var homeFragment: Fragment = HomeFragment()
    var hobbyEventListFragment: Fragment = HobbyEventListFragment()
    var favoriteListFragment: Fragment = FavoriteListFragment()
    var settingsFragment: Fragment = SettingsFragment()
    var mapFragment: Fragment = MapFragment()
    var promotionFragment: Fragment = PromotionFragment()
    var fragmentManager: FragmentManager = supportFragmentManager
    lateinit var activeFragment: Fragment
    var isMapFragment = false
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        val TAG = "onCreate"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Harrastuspassi"

        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        //BOTTOM NAVIGATION BAR
        toolbar = supportActionBar!!
        bottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener (onNavigationItemSelectedListener)
        fragmentManager.beginTransaction().add(R.id.navigation_container, homeFragment).commit()
        activeFragment = homeFragment
        fragmentManager.beginTransaction().add(R.id.navigation_container, hobbyEventListFragment).hide(hobbyEventListFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, favoriteListFragment).hide(favoriteListFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, settingsFragment).hide(settingsFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, promotionFragment).hide(promotionFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, mapFragment).hide(mapFragment).commit()

        //openFragment(hobbyEventListFragment)

        // OBTAIN THE FirebaseAnalytics INSTANCE
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

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
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }
    }

    fun performListClick() {
        switchFragment(hobbyEventListFragment, getString(R.string.hobbies))
        navigationView.selectedItemId = R.id.navigation_list
    }

    fun switchBetweenMapAndListFragment() {
        if(isMapFragment) {
            switchFragment(hobbyEventListFragment, getString(R.string.hobbies))
            bottomNavigationView.menu.findItem(R.id.navigation_list).setIcon(ContextCompat.getDrawable(this, R.drawable.list_icon))
            isMapFragment = false
        } else {
            switchFragment(mapFragment, "")
            bottomNavigationView.menu.findItem(R.id.navigation_list).setIcon(ContextCompat.getDrawable(this, R.drawable.map_icon))
            isMapFragment = true
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId) {
            R.id.navigation_home -> {
                switchFragment(homeFragment, "Harrastuspassi")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                if(isMapFragment) {
                    switchFragment(mapFragment, "")
                } else {
                    switchFragment(hobbyEventListFragment, getString(R.string.hobbies))
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorites -> {
                switchFragment(favoriteListFragment, getString(R.string.favorites))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_promotions -> {
                switchFragment(promotionFragment, getString(R.string.promotions))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                switchFragment(settingsFragment, getString(R.string.settings))
                return@OnNavigationItemSelectedListener true
            }
        }

        false

    }

    override fun onPause() {
        super.onPause()
        var filters = loadFilters(this)
        filters.isModified = false
        saveFilters( filters,this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    private fun switchFragment(fragment: Fragment, fragmentTitle: String) {
        fragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
        title = fragmentTitle
    }
}




