package fi.haltu.harrastuspassi.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.ncorti.slidetoact.SlideToActView
import com.ncorti.slidetoact.SlideToActView.OnSlideCompleteListener
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.PromotionListAdapter
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Promotion
import fi.haltu.harrastuspassi.utils.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class PromotionFragment : Fragment() {
    private lateinit var promotionListView: RecyclerView
    private lateinit var comingSoonTextView: TextView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private var promotionList = ArrayList<Promotion>()
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var usedPromotions: HashSet<Int> = HashSet()
    private var searchText: String? = null
    private lateinit var filters: Filters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this.context!!)
        val view: View = inflater.inflate(R.layout.fragment_promotion, container, false)
        //PROGRESS BAR
        progressBar = view.findViewById(R.id.promotion_progressbar)
        refreshLayout = view.findViewById(R.id.swipe_refresh_list)
        refreshLayout.setOnRefreshListener {
            GetPromotions().execute()
        }
        filters = loadFilters(activity!!)
        // SEARCH_VIEW
        searchView = view.findViewById(R.id.promotion_search)
        searchView.setOnClickListener {
            searchView.isIconified = false
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchText = query
                GetPromotions().execute()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText
                GetPromotions().execute()
                return false
            }
        })
        usedPromotions = loadUsedPromotions(this.activity!!)
        promotionListView = view.findViewById(R.id.promotion_list_view)
        comingSoonTextView = view.findViewById(R.id.promotion_coming_soon)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this.context!!)
        GetPromotions().execute()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this.context!!)
        return view
    }

    private fun promotionItemClicked(promotion: Promotion, hobbyImage: ImageView) {
        // FIREBASE ANALYTICS
        val bundle = Bundle()
        bundle.putString("promotionName", promotion.title)
        if (promotion.organizer != null) {
            bundle.putInt("organizerName", promotion.organizer)
        } else {
            bundle.putString("organizerName", "no organization")
        }
        bundle.putString("municipality", promotion.municipality)

        firebaseAnalytics.logEvent("viewPromotion", bundle)
        val dialog = Dialog(this.context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_promotion_detail)
        //IMAGE
        val imageView = dialog.findViewById<ImageView>(R.id.promotion_dialog_image)
        Picasso.with(context)
            .load(promotion.imageUrl)
            .placeholder(R.drawable.harrastuspassi_lil_kel)
            .error(R.drawable.harrastuspassi_lil_kel)
            .into(imageView)
        //TITLE
        val titleText = dialog.findViewById<TextView>(R.id.promotion_dialog_title)
        titleText.text = promotion.title

        //LOCATION
        dialog.findViewById<TextView>(R.id.promotion_location).text = promotion.location.name
        dialog.findViewById<TextView>(R.id.promotion_location_address).text = "${promotion.location.address}, ${promotion.location.city}"
        dialog.findViewById<TextView>(R.id.promotion_location_zipcode).text = promotion.location.zipCode

        //DESCRIPTION
        val descriptionText = dialog.findViewById<TextView>(R.id.promotion_dialog_description)
        descriptionText.setTextWithLinkSupport(promotion.description) {
            var url = it
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://$url";
            // Opens url in browser
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }

        //DATE
        val durationText = dialog.findViewById<TextView>(R.id.promotion_dialog_duration)

        durationText.text = "${activity!!.getString(R.string.available)}: ${convertToDateRange(
            promotion.startDate,
            promotion.endDate
        )}"

        //USED AMOUNT
        val usedAmountText = dialog.findViewById<TextView>(R.id.promotion_dialog_used_amount)
        usedAmountText.text = "${activity!!.getString(R.string.promotions_left)}: ${promotion.availableCount - promotion.usedCount}"

        //CLOSE_ICON
        val closeIcon = dialog.findViewById<ImageView>(R.id.dialog_close_button)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }
        // USE PROMOTION SLIDE BUTTON
        val slideButton = dialog.findViewById<SlideToActView>(R.id.promotion_dialog_slide_button)
        val promotionUsedText = dialog.findViewById<TextView>(R.id.promotion_dialog_used)

        slideButton.onSlideCompleteListener = object : OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                // FIREBASE ANALYTICS
                val bundle = Bundle()
                bundle.putString("promotionName", promotion.title)
                if (promotion.organizer != null) {
                    bundle.putInt("organizerName", promotion.organizer)
                } else {
                    bundle.putString("organizerName", "no organization")
                }
                bundle.putString("municipality", promotion.municipality)

                promotion.isUsed = true
                usedPromotions.add(promotion.id)
                saveUsedPromotions(usedPromotions, activity!!)
                slideButton.visibility = View.INVISIBLE
                promotionUsedText.text = activity!!.getString(R.string.promotions_used)
                promotionUsedText.visibility = View.VISIBLE
                promotionListView.adapter!!.notifyDataSetChanged()
                promotionListView.adapter!!.notifyDataSetChanged()

                firebaseAnalytics.logEvent("usePromotion", bundle)
                PostPromotion(promotion.id).execute()
                GetPromotions().execute()
                dialog.dismiss()
            }
        }

        if (promotion.isUsed) {
            slideButton.visibility = View.INVISIBLE
            promotionUsedText.text = activity!!.getString(R.string.promotions_used)
            promotionUsedText.visibility = View.VISIBLE
            promotionListView.adapter!!.notifyDataSetChanged()
        }

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.show()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //hidden == false is almost same than onResume
        if (!hidden) {
            filters = loadFilters(activity!!)
            usedPromotions = loadUsedPromotions(this.activity!!)
            GetPromotions().execute()
        }
    }

    override fun onResume() {
        super.onResume()
        filters = loadFilters(activity!!)
        usedPromotions = loadUsedPromotions(this.activity!!)
    }


    companion object {
        const val ERROR = "error"
        const val MAX_DISTANCE = 45 // Km
    }

    internal inner class PostPromotion(private val promotionId: Int) :
        AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("promotion", promotionId.toString())
                .build()
            val request = Request.Builder()
                .url(getString(R.string.API_URL) + "benefits/")
                .header("Authorization", getString(R.string.PROMOTION_TOKEN))
                .post(requestBody)
                .build()
            client.newCall(request).execute()
                .use { response -> return response.body.toString() }
        }
    }

    internal inner class GetPromotions : AsyncTask<Void, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }
        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + createPromotionQueryUrl(filters, searchText, MAX_DISTANCE)).readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when (result) {
                ERROR -> {
                }
                else -> {
                    try {
                        val mJsonArray = JSONArray(result)
                        promotionList.clear()
                        for (i in 0 until mJsonArray.length()) {
                            val sObject = mJsonArray.get(i).toString()
                            val hobbyObject = JSONObject(sObject)
                            val promotion = Promotion(hobbyObject)
                            if (usedPromotions.contains(promotion.id)) {
                                promotion.isUsed = true
                            }
                            if (promotion.usedCount < promotion.availableCount) {
                                promotionList.add(promotion)
                            }
                        }

                        // Move used promotions to end of list
                        val promotionsByIsUsed = promotionList.groupBy{promotion -> promotion.isUsed}
                        promotionList = ArrayList(
                            (promotionsByIsUsed[false] ?: ArrayList()) + (promotionsByIsUsed[true] ?: ArrayList()))

                        val promotionListAdapter = PromotionListAdapter(
                            context!!,
                            promotionList
                        ) { promotion: Promotion, promotionImage: ImageView ->
                            promotionItemClicked(
                                promotion,
                                promotionImage
                            )
                        }
                        promotionListView.apply {
                            layoutManager = LinearLayoutManager(activity)
                            adapter = promotionListAdapter
                        }
                        if (promotionList.isEmpty()) {
                            comingSoonTextView.text = activity!!.getString(R.string.no_promotions_found)
                            comingSoonTextView.visibility = View.VISIBLE
                        } else {
                            comingSoonTextView.visibility = View.GONE
                        }

                    } catch (e: JSONException) {

                    }
                }
            }
            progressBar.visibility = View.INVISIBLE
            refreshLayout.isRefreshing = false
        }
    }
}