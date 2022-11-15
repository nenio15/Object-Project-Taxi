package com.example.taxicar_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taxicar_app.databinding.FragmentMainBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private const val ARG_PARAM1 = "param1"
    //private const val ARG_PARAM2 = "param2"

    /**
     * A simple [Fragment] subclass.
     * Use the [SignInFragment.newInstance] factory method to
     * create an instance of this fragment.
     */
    class MainFragment : Fragment() {
        var binding: FragmentMainBinding? = null
        // TODO: Rename and change types of parameters
        //private var param1: String? = null
        //private var param2: String? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
                //param1 = it.getString(ARG_PARAM1)
                //param2 = it.getString(ARG_PARAM2)
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentMainBinding.inflate(inflater, container, false)
            // Inflate the layout for this fragment

            //private val mActivity = activity as MainActivity

            binding?.startBtn?.setOnClickListener {
                Log.d("MAIN", "to sign in")
                val mActivity = activity as MainActivity
                //mActivity.replaceFragment(AlarmFragment.newInstance())    //for test
                mActivity.replaceFragment(SignInFragment.newInstance())

            }
            return binding?.root//inflater.inflate(R.layout.fragment_sign_in, container, false)
        }

        companion object {
            // TODO: Rename and change types and number of parameters
            @JvmStatic
            fun newInstance() =
                MainFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
        }
    }