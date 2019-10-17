package fi.haltu.harrastuspassi.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import fi.haltu.harrastuspassi.R


class FavoriteListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_favorite_list, container, false)
    }



}