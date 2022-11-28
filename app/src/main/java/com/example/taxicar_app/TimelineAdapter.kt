package com.example.taxicar_app

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.taxicar_app.databinding.ListTimelineBinding
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.contentValuesOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.list_timeline.view.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class Timeline( val time: String, val waiting: Long ){
    //override fun 설정값 time만 하나. time + waiting 하나.
}
class Timedata( var car: String, var togo: String)

// db 필요함?
class TimelineAdapter(private val times: ArrayList<Timeline>, private val mActivity: MenuActivity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //val binding = ListTimelineBinding.inflate(LayoutInflater.from(parent.context))
        //return Holder(binding, mActivity, db)
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
            //다른거 list설정.
            itemView.txt_time.text = timeline.time
            itemView.txt_waiting.text = timeline.waiting.toString()

            itemView.setOnClickListener{
                // 아마 이것만 있으면 됨..
                mActivity.goIntent(timeline.time)
                // 현재 시각 구하기... 이게 왜 여기있지?
                val sf = SimpleDateFormat("yy/MM/dd HH:mm", Locale.KOREA)
                sf.timeZone = TimeZone.getTimeZone("Asiz/Seoul")
            }


        }

    }

}