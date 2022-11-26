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

/*
enum class Timeline {
    500,
    530,
    600
}

*/

class Timeline( val time: String, val waiting: String ){
    //override fun 설정값 time만 하나. time + waiting 하나.
}
class Timedata( var car: String, var togo: String)

class TimelineAdapter(val times: Array<Timeline>, val mActivity: MenuActivity, val db: FirebaseFirestore)
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
            binding.txtWaiting.text = timeline.waiting

            binding.root.setOnClickListener{
                // 아마 이것만 있으면 됨..
                mActivity.goIntent(timeline.time)


                // 현재 시각 구하기... 이게 왜 여기있지?
                val sf = SimpleDateFormat("yy/MM/dd HH:mm", Locale.KOREA)
                sf.timeZone = TimeZone.getTimeZone("Asiz/Seoul")

                /*
                val data = hashMapOf(
                    "user" to "홍길동",    // register's name
                    "time" to timeline.time // reserve timeline 어차피 얼마없..?
                    // "day" to         // 이걸로 요일 구분... 이건 언제 또 쓰냐?
                )
                val timeReserve = db.collection("Reserve")
                    .document(mActivity.whereTogo.car)
                    .collection(mActivity.whereTogo.togo)
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(binding.root.context, "${timeline.time} 시간대에 !대기자로 설정되었습니다.",
                            Toast.LENGTH_SHORT)
                            .show()
                        Log.d("Timeline", "Document added: $it")
                    }
                    .addOnFailureListener{ e ->
                        Toast.makeText(binding.root.context, "전송실패..", Toast.LENGTH_SHORT).show()
                        Log.d("Timeline", "Error occcurs: $e")
                    }

                 */
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
            //val mActivity = activity as MainActivity


            // 클릭 시 대기자 생성, 및 단톡방 들어가기


        }


    }

}