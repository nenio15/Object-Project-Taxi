package com.example.taxicar_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.FragmentTimelineBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [timelineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class timelineFragment : Fragment() {

    var binding: FragmentTimelineBinding? = null
    val timelines = arrayOf(
        Timeline("9:00", "홍길동 김철수 김영희"),
        Timeline("10:00", ""),
        Timeline("11:00", ""),
        Timeline("12:00", ""),
        Timeline("13:00", ""),
        Timeline("14:00", "홍길동"),
        Timeline("15:00", ""),
        Timeline("16:00", ""),
        Timeline("17:00", ""),
        Timeline("18:00", ""),
        Timeline("19:00", ""),
        Timeline("20:00", ""),
        Timeline("21:00", ""),
        Timeline("22:00", ""),
        Timeline("23:00", ""),
        Timeline("24:00", ""),
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val mActivity = activity as MenuActivity
        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        binding?.recTimelines?.layoutManager = LinearLayoutManager(mActivity)
        val curTimelines = TimelineAdapter(timelines, mActivity)
        binding?.recTimelines?.adapter = curTimelines

        /*
        binding?.recTimelines?.setOnClickListener{
            Log.d("rec", "just click")
            val intent = Intent(mActivity, SubActivity::class.java)
            startActivity(intent)
        }
         */

        // Inflate the layout for this fragment
        return binding?.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment timelineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            timelineFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}