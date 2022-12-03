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

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentMainBinding.inflate(inflater, container, false)

            binding?.startBtn?.setOnClickListener {
                Log.d("MAIN", "to sign in")
                val mActivity = activity as MainActivity
                mActivity.replaceFragment(SignInFragment.newInstance())

            }
            return binding?.root
        }

        companion object {
            @JvmStatic
            fun newInstance() =
                MainFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
        }
    }