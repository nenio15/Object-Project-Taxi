package com.example.taxicar_app


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.taxicar_app.databinding.FragmentChoiceBinding
import com.example.taxicar_app.databinding.FragmentTimelineBinding
import kotlinx.android.synthetic.main.activity_chat.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChoiceFragment : Fragment() {
    var binding: FragmentChoiceBinding? = null

    // TODO: Rename and change types of parameters
    private var toSchool: Boolean? = null
    //private var byTaxi: Boolean? = null

    fun divideLine(togo: Boolean?, btn: Int, mActivity: MenuActivity){
        //대충, index에 따라서, 0이면 taxi & school, 1이면 carp & dome

        if(togo == true) {
            Log.d("CHOICE", "line to school, $btn")
            mActivity.whereTogo.car = "Taxi"
            findNavController().navigate(R.id.action_choiceFragment_to_timelineFragment)
            //아래 말고, top_toTop 을 아래 parent로 바꾸는거 업냐, 애니메이션
            return
        }
        else if(togo == false) {
            Log.d("CHOICE", "line to dormi, $btn")
            mActivity.whereTogo.car = "CarPool"
            findNavController().navigate(R.id.action_choiceFragment_to_timelineFragment)
            return
        }

        when(btn){
            0 -> {
                Log.d("CHOICE", "toSchool...")
                toSchool = true
                mActivity.whereTogo.togo = "toSchool"
            }
            1 -> {
                Log.d("CHOICE", "toDormi...")
                toSchool = false
                mActivity.whereTogo.togo = "toDormi"
            }
        }
        binding?.goTaxiClick?.text = "Taxi"
        binding?.goCarpoolClick?.text = "CAR POOL"
        return

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoiceBinding.inflate(inflater, container, false)
        val mActivity = activity as MenuActivity
        //mActivity.binding.listBack.visibility = View.GONE

        binding?.goTaxiClick?.setOnClickListener{
            divideLine(toSchool, 0, mActivity)
        }
        binding?.goCarpoolClick?.setOnClickListener{
            divideLine(toSchool, 1, mActivity)
        }
        mActivity.binding.btnTest2.setOnClickListener {

            binding?.goTaxiClick?.text = "Dormitory\n-> School"
            binding?.goCarpoolClick?.text = "School\n-> Dormitory"
            toSchool = null
            //mActivity.replaceFragment(ChoiceFragment.newInstance())
            Log.d("CHOICE", "back to dest")

        }

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
         * @return A new instance of fragment fragment_sign_in.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ChoiceFragment().apply {
                arguments = Bundle().apply {
                    //1.back버튼의 경우, goto로 바꿈. line에서 돌아오는 경우, by로 바꿈(text문제 + 변수)
                    //toSchool = togo
                }
            }
    }
}