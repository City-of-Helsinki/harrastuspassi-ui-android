package fi.haltu.harrastuspassi.fragments.home

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R


class HomeFragment : Fragment() {
    lateinit var searchEditText: EditText
    lateinit var searchContainer: ConstraintLayout
    lateinit var searchIcon: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        //SEARCH
        searchEditText = view.findViewById(R.id.home_search)
        searchContainer = view.findViewById(R.id.search_container)
        searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                searchContainer.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.white))
            } else {
                searchContainer.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.white60))
            }
        }
        searchEditText.setOnKeyListener { v, keyCode, event ->
            // User presses "enter" on keyboard
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                search(searchEditText.text.toString())
                view.clearFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        searchIcon = view.findViewById(R.id.home_search_icon)
        searchIcon.setOnClickListener {
            search(searchEditText.text.toString())
        }
        setHobbies(view)
        return view
    }

    private fun setHobbies(parentView: View) {
        var promotedPromotionView: View = parentView.findViewById(R.id.home_promoted_hobby)
        //IMAGE
        Picasso.with(this.context)
            .load("URL")
            .placeholder(R.drawable.harrastuspassi_lil_kel)
            .error(R.drawable.harrastuspassi_lil_kel)
            .into(promotedPromotionView.findViewById<ImageView>(R.id.home_promoted_image))
        //TITLE
        promotedPromotionView.findViewById<TextView>(R.id.home_promoted_title).text = "TitleText"
        //DESCRIPTION
        promotedPromotionView.findViewById<TextView>(R.id.home_promoted_description).text = "Nullam volutpat tempor metus vel rhoncus. Fusce sodales diam risus, nec hendrerit augue fermentum eu. Donec vitae erat ut libero molestie congue in vitae ligula."
        //DURATION
        promotedPromotionView.findViewById<TextView>(R.id.home_promoted_duration).text = "Voimassa: 10.11 - 15.11.2019"
    }

    private fun search(searchStr: String) {
        //TODO search logic
        Toast.makeText(this.context, searchStr, Toast.LENGTH_SHORT).show()
    }
}