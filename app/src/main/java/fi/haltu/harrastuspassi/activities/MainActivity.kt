package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.fragments.*
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.saveFilters
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: ActionBar
    lateinit var menu: Menu
    var homeFragment: Fragment = HomeFragment()
    var hobbyEventListFragment: Fragment = HobbyEventListFragment()
    var favoriteListFragment: Fragment = FavoriteListFragment()
    var settingsFragment: Fragment = SettingsFragment()
    var mapFragment: Fragment = MapFragment()
    var promotionFragment: Fragment = PromotionFragment()
    var fragmentManager: FragmentManager = supportFragmentManager
    lateinit var activeFragment: Fragment

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
        fragmentManager.beginTransaction().add(R.id.navigation_container, homeFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, hobbyEventListFragment).hide(hobbyEventListFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, favoriteListFragment).hide(favoriteListFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, settingsFragment).hide(settingsFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, promotionFragment).hide(promotionFragment).commit()
        fragmentManager.beginTransaction().add(R.id.navigation_container, mapFragment).hide(mapFragment).commit()

        activeFragment = hobbyEventListFragment
        //openFragment(hobbyEventListFragment)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                val intent = Intent(this, FilterViewActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                return true
            }
            R.id.map -> {
                fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit()
                activeFragment = mapFragment
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun performListClick() {
        fragmentManager.beginTransaction().hide(activeFragment).show(hobbyEventListFragment).commit()
        activeFragment = hobbyEventListFragment
        navigationView.selectedItemId = R.id.navigation_list
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId) {
            R.id.navigation_home -> {
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit()
                activeFragment = homeFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                fragmentManager.beginTransaction().hide(activeFragment).show(hobbyEventListFragment).commit()
                activeFragment = hobbyEventListFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorites -> {
                fragmentManager.beginTransaction().hide(activeFragment).show(favoriteListFragment).commit()
                activeFragment = favoriteListFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                fragmentManager.beginTransaction().hide(activeFragment).show(settingsFragment).commit()
                activeFragment = settingsFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_promotions -> {
                fragmentManager.beginTransaction().hide(activeFragment).show(promotionFragment).commit()
                activeFragment = promotionFragment
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

   override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
   }
}




