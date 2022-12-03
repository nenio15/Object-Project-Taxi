package com.example.taxicar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_timeline.view.*
import java.text.SimpleDateFormat
import java.util.*

class Timeline( val time: String, val waiting: Long )
class Timedata( var car: String, var togo: String)

class TimelineAdapter(private val times: ArrayList<Timeline>, private val mActivity: MenuActivity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(mActivity).inflate(R.layout.list_timeline, parent, false)
        return ViewHolder(view, mActivity)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as ViewHolder
        viewHolder.bind(times[position])
    }

    override fun getItemCount() = times.size

    class ViewHolder(itemView: View, private val mActivity: MenuActivity)
        : RecyclerView.ViewHolder(itemView) {
        fun bind(timeline: Timeline){
            // TODO 갈려는 목적지 및 수단 표시 + 대기자 이미지로 변경
            itemView.txt_time.text = timeline.time
            itemView.txt_waiting.text = timeline.waiting.toString()

            itemView.setOnClickListener{
                mActivity.goIntent(timeline.time)
            }


        }

    }

}