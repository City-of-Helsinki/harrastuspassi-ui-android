package fi.haltu.harrastuspassi.fragments.home

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.ncorti.slidetoact.SlideToActView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.PromotionHorizontalListAdapter
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
import android.view.ViewManager
import androidx.constraintlayout.widget.Constraints


class HomePromotionsFragment : Fragment() {
    lateinit var rootView: View
    lateinit var popularPromotionsListView: RecyclerView
    lateinit var userPromotionsListView: RecyclerView
    lateinit var userPromotionText: TextView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var usedPromotions: HashSet<Int> = HashSet()
    private var popularPromotionList = ArrayList<Promotion>()
    private var filters = Filters()
    private var promotedPromotionID = 0

    companion object {
        const val ERROR = "error"
        const val MAX_ITEM_AMOUNT = 5 //max amount of promotions to show in recyclerViews
        const val MIN_ITEM_AMOUNT = 1 //min amount of promotions to show in recyclerViews
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home_promotions, container, false)
        setHasOptionsMenu(true)

        //Loads filters
        filters = loadFilters(this.activity!!)

        //PROMOTIONS LISTS
        popularPromotionsListView = rootView.findViewById(R.id.home_popular_promotion_list)
        userPromotionsListView = rootView.findViewById(R.id.home_user_promotion_list)
        usedPromotions = loadUsedPromotions(this.activity!!)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this.context!!)
        userPromotionText = rootView.findViewById(R.id.user_promotion_text_label)

        GetPromotions().execute()
        return rootView
    }

    private fun setPromotions(parentView: View, promotionList: ArrayList<Promotion>) {
        if (promotionList.isNotEmpty()) {
            var popularPromotions = ArrayList<Promotion>()
            popularPromotions.addAll(promotionList.shuffled())

            //PROMOTED PROMOTION
            val promotedPromotion = popularPromotions[0]
            promotedPromotionID = promotedPromotion.id

            popularPromotions.removeAt(0)

            //IMAGE
            Picasso.with(this.context)
                .load(promotedPromotion.imageUrl)
                .placeholder(R.drawable.harrastuspassi_lil_kel)
                .error(R.drawable.harrastuspassi_lil_kel)
                .into(parentView.findViewById<ImageView>(R.id.home_promoted_image))
            //TITLE
            parentView.findViewById<TextView>(R.id.home_promoted_title).text =
                promotedPromotion.title
            //DESCRIPTION
            parentView.findViewById<TextView>(R.id.home_promoted_description).text =
                promotedPromotion.description
            parentView.findViewById<TextView>(R.id.home_promoted_duration).text =
                "${convertToDateRange(promotedPromotion.startDate, promotedPromotion.endDate)}"
            parentView.findViewById<CardView>(R.id.home_promoted_promotion).setOnClickListener {
                promotionsItemClicked(promotedPromotion)
            }
            if (promotedPromotion.isUsed) {
                parentView.findViewById<ConstraintLayout>(R.id.constraintLayout).background =
                    ContextCompat.getDrawable(this.context!!, R.color.blackOpacity40)
                parentView.findViewById<TextView>(R.id.home_promoted_duration).text =
                    activity!!.getString(R.string.promotions_used)
            }
            //POPULAR PROMOTION LIST
            when {
                popularPromotions.size > MAX_ITEM_AMOUNT -> {
                    popularPromotionsListView.apply {
                        layoutManager =
                            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = PromotionHorizontalListAdapter(
                            context!!,
                            popularPromotions.subList(0, MAX_ITEM_AMOUNT)
                        )
                        { promotion: Promotion -> promotionsItemClicked(promotion) }
                    }
                }
                popularPromotions.size > MIN_ITEM_AMOUNT -> {
                    popularPromotionsListView.apply {
                        layoutManager =
                            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = PromotionHorizontalListAdapter(
                            context!!,
                            popularPromotions
                        ) { promotion: Promotion -> promotionsItemClicked(promotion) }
                    }
                }
                else -> popularPromotionsListView.visibility = View.INVISIBLE
            }
            //USER PROMOTION LIST
            when {
                promotionList.size >= MAX_ITEM_AMOUNT -> {
                    userPromotionsListView.apply {
                        layoutManager =
                            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = PromotionHorizontalListAdapter(
                            context!!,
                            promotionList.subList(0, MAX_ITEM_AMOUNT)
                        )
                        { promotion: Promotion -> promotionsItemClicked(promotion) }
                    }
                }
                promotionList.size >= MIN_ITEM_AMOUNT -> {
                    userPromotionsListView.apply {
                        layoutManager =
                            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = PromotionHorizontalListAdapter(
                            context!!,
                            promotionList
                        )
                        { promotion: Promotion -> promotionsItemClicked(promotion) }
                    }
                }
                else -> {
                    userPromotionsListView.visibility = View.INVISIBLE
                    rootView.findViewById<TextView>(R.id.user_promotion_text_label).visibility = View.INVISIBLE
                }
            }
        } else {
            //TODO MUST FIX!!
            parentView.visibility = View.INVISIBLE
            parentView.layoutParams = Constraints.LayoutParams(0,400)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            usedPromotions = loadUsedPromotions(this.activity!!)
            GetPromotions().execute()
        }
    }

    private fun promotionsItemClicked(promotion: Promotion) {
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
        //DESCRIPTION
        val descriptionText = dialog.findViewById<TextView>(R.id.promotion_dialog_description)
        descriptionText.text = promotion.description
        //DATE
        val durationText = dialog.findViewById<TextView>(R.id.promotion_dialog_duration)

        durationText.text = "${activity!!.getString(R.string.available)}: ${convertToDateRange(
            promotion.startDate,
            promotion.endDate
        )}"
        //CLOSE_ICON
        val closeIcon = dialog.findViewById<ImageView>(R.id.dialog_close_button)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }
        // USE PROMOTION SLIDE BUTTON
        val slideButton = dialog.findViewById<SlideToActView>(R.id.promotion_dialog_slide_button)
        val promotionUsedText = dialog.findViewById<TextView>(R.id.promotion_dialog_used)

        slideButton.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
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
                popularPromotionsListView.adapter?.notifyDataSetChanged()
                userPromotionsListView.adapter?.notifyDataSetChanged()
                if(promotedPromotionID == promotion.id) {
                    rootView.findViewById<ConstraintLayout>(R.id.constraintLayout).background =
                        ContextCompat.getDrawable(context!!, R.color.blackOpacity40)
                    rootView.findViewById<TextView>(R.id.home_promoted_duration).text =
                        activity!!.getString(R.string.promotions_used)
                }

                    firebaseAnalytics.logEvent("usePromotion", bundle)
                PostPromotion(promotion.id).execute()
            }
        }

        if (promotion.isUsed) {
            slideButton.visibility = View.INVISIBLE
            promotionUsedText.text = activity!!.getString(R.string.promotions_used)
            promotionUsedText.visibility = View.VISIBLE

            popularPromotionsListView.adapter?.notifyDataSetChanged()
            userPromotionsListView.adapter?.notifyDataSetChanged()
        }

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.show()
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

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + createPromotionQueryUrl(filters)).readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when (result) {
                ERROR -> {
                    userPromotionText.visibility = View.INVISIBLE
                }
                else -> {
                    try {
                        val mJsonArray = JSONArray(result)
                        popularPromotionList.clear()
                        for (i in 0 until mJsonArray.length()) {
                            val sObject = mJsonArray.get(i).toString()
                            val hobbyObject = JSONObject(sObject)
                            val promotion = Promotion(hobbyObject)
                            if (usedPromotions.contains(promotion.id)) {
                                promotion.isUsed = true
                            }
                            if (promotion.usedCount < promotion.availableCount) {
                                popularPromotionList.add(promotion)
                            }
                        }

                        setPromotions(rootView, popularPromotionList)

                    } catch (e: JSONException) {
                        userPromotionText.visibility = View.INVISIBLE

                    }
                }
            }
        }
    }
}