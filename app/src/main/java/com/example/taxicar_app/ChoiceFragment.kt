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

class ChoiceFragment : Fragment() {
    var binding: FragmentChoiceBinding? = null

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
            Log.d("CHOICE", "back to dest")

        }

        return binding?.root
    }

}