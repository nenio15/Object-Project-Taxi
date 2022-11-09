package com.example.taxicar_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.taxicar_app.databinding.ListTimelineBinding
import androidx.core.content.ContextCompat.startActivity


class Timeline( val time: String, val waiting: String ){
    //override fun 설정값 time만 하나. time + waiting 하나.

}

class TimelineAdapter(val times: Array<Timeline>)
    : RecyclerView.Adapter<TimelineAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListTimelineBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(times[position])
    }

    override fun getItemCount() = times.size

    class Holder(private val binding: ListTimelineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timeline: Timeline){
            //다른거 list설정.
            binding.txtTime.text = timeline.time
            binding.txtWaiting.text = timeline.waiting

            //val mActivity = activity as MainActivity


            //이거는 클릭시 문자 이벤트
            binding.root.setOnClickListener{
                Toast.makeText(binding.root.context, "${timeline.time} 시간대에 !대기자로 설정되었습니다.",
                    Toast.LENGTH_SHORT)
                    .show()

                //Intent intent = new Intent(getActivity(), SubActivity::class.java)
                //val intent = Intent(mActivity, SubActivity::class.java)
                //startActivity(intent)
            }

        }


    }

}