package fi.haltu.harrastuspassi.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ncorti.slidetoact.SlideToActView
import com.ncorti.slidetoact.SlideToActView.OnSlideCompleteListener
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.PromotionListAdapter
import fi.haltu.harrastuspassi.models.Promotion
import fi.haltu.harrastuspassi.utils.convertToDateRange
import fi.haltu.harrastuspassi.utils.loadUsedPromotions
import fi.haltu.harrastuspassi.utils.saveUsedPromotions
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import kotlin.collections.ArrayList

class PromotionFragment : Fragment(){
    private lateinit var promotionListView: RecyclerView
    private lateinit var comingSoonTextView: TextView
    private var promotionList = ArrayList<Promotion>()
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var usedPromotions: HashSet<Int> = HashSet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_promotion, container, false)
        refreshLayout = view.findViewById(R.id.swipe_refresh_list)

        refreshLayout.setOnRefreshListener {
            GetPromotions().execute()
        }
        usedPromotions = loadUsedPromotions(this.activity!!)
        promotionListView = view.findViewById(R.id.promotion_list_view)
        comingSoonTextView = view.findViewById(R.id.promotion_coming_soon)
        GetPromotions().execute()
        return view
    }
    private fun hobbyItemClicked(promotion: Promotion, hobbyImage: ImageView) {
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


        durationText.text = "${activity!!.getString(R.string.available)}: ${convertToDateRange(promotion.startDate, promotion.endDate)}"
        //CLOSE_ICON
        val closeIcon = dialog.findViewById<ImageView>(R.id.dialog_close_button)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }
        // USE PROMOTION SLIDE BUTTON
        val slideButton = dialog.findViewById<SlideToActView>(R.id.promotion_dialog_slide_button)
        val promotionUsedText = dialog.findViewById<TextView>(R.id.promotion_dialog_used)

        slideButton.onSlideCompleteListener = object : OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                promotion.isUsed = true
                usedPromotions.add(promotion.id)
                saveUsedPromotions(usedPromotions, activity!!)
                slideButton.visibility = View.INVISIBLE
                promotionUsedText.text = activity!!.getString(R.string.promotions_used)
                promotionUsedText.visibility = View.VISIBLE
                promotionListView.adapter!!.notifyDataSetChanged()
                promotionListView.adapter!!.notifyDataSetChanged()
            }
        }

        if (promotion.isUsed) {
            slideButton.visibility = View.INVISIBLE
            promotionUsedText.text = activity!!.getString(R.string.promotions_used)
            promotionUsedText.visibility = View.VISIBLE
            promotionListView.adapter!!.notifyDataSetChanged()
        }

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.show()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //hidden == false is almost same than onResume
        if(!hidden) {
            usedPromotions = loadUsedPromotions(this.activity!!)
        }
    }

    override fun onResume() {
        super.onResume()
        usedPromotions = loadUsedPromotions(this.activity!!)
    }


    companion object {
        const val ERROR = "error"
    }

    internal inner class GetPromotions : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "promotions").readText()
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
                            if(usedPromotions.contains(promotion.id)) {
                                promotion.isUsed = true
                            }
                            promotionList.add(promotion)
                        }
                        val promotionListAdapter = PromotionListAdapter(context!!, promotionList){ promotion: Promotion, promotionImage: ImageView -> hobbyItemClicked(promotion, promotionImage)}
                        promotionListView.apply {
                            layoutManager = LinearLayoutManager(activity)
                            adapter = promotionListAdapter
                        }
                        if(promotionList.isEmpty()) {
                            comingSoonTextView.text = activity!!.getString(R.string.coming_soon)
                            comingSoonTextView.visibility = View.VISIBLE
                        }

                    } catch(e: JSONException) {

                    }
                }
            }
            refreshLayout.isRefreshing = false
        }
    }
}