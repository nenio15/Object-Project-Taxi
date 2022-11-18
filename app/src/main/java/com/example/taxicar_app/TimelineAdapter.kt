package com.example.taxicar_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.taxicar_app.databinding.ListTimelineBinding
import androidx.core.content.ContextCompat.startActivity


class Timeline( val time: String, val waiting: String ){
    //override fun 설정값 time만 하나. time + waiting 하나.

}

public class TimelineAdapter(val times: Array<Timeline>, val mActivity: MenuActivity)
    : RecyclerView.Adapter<TimelineAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListTimelineBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding, mActivity)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(times[position])
        /*
        holder.itemView.setOnClickListener(View.OnClickListener(){
            public fun onClick(v: View){
                val intent = Intent(this, SubActivity::class.java)
                startActivity(intent)
            }
        })

         */
    }

    override fun getItemCount() = times.size

    public class Holder(private val binding: ListTimelineBinding, val mActivity: MenuActivity) : RecyclerView.ViewHolder(binding.root) {
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
                mActivity.goIntent()

            }

        }


    }

}