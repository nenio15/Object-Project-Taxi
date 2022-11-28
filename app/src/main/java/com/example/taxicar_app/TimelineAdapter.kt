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
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class Timeline( val time: String, val waiting: Int ){
    //override fun 설정값 time만 하나. time + waiting 하나.
}
class Timedata( var car: String, var togo: String)

// db 필요함?
class TimelineAdapter(private val times: ArrayList<Timeline>, private val mActivity: MenuActivity, private val db: FirebaseFirestore)
    : RecyclerView.Adapter<TimelineAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListTimelineBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding, mActivity, db)
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

    class Holder(private val binding: ListTimelineBinding, val mActivity: MenuActivity, val db: FirebaseFirestore)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(timeline: Timeline){
            //다른거 list설정.
            binding.txtTime.text = timeline.time
            binding.txtWaiting.text = timeline.waiting.toString()

            binding.root.setOnClickListener{
                // 아마 이것만 있으면 됨..
                mActivity.goIntent(timeline.time)

                // 현재 시각 구하기... 이게 왜 여기있지?
                val sf = SimpleDateFormat("yy/MM/dd HH:mm", Locale.KOREA)
                sf.timeZone = TimeZone.getTimeZone("Asiz/Seoul")
            }

            /*
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener{ snapshots, e ->
                    // 오류 발생
                    if( e != null ) {
                        Log.d("Listner", "Listen failed: $e")
                        return@addSnapshotListener
                    }

                    // 원치 않는 문서 무시
                    if(snapshots!!.metadata.isFromCache) return@addSnapshotListener

                    // 문서 수신
                    for( doc in snapshots.documentChanges) {
                        if( doc.document["time"] != timeline.time)
                    }
                }


             */

        }

    }

}