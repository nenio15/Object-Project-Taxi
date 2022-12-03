package com.example.taxicar_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.FragmentTimelineBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class timelineFragment : Fragment() {
    private var binding: FragmentTimelineBinding? = null
    private val db = Firebase.firestore
    private lateinit var timeList: ArrayList<Timeline>
    private lateinit var timeLine: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mActivity = activity as MenuActivity
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
        timeList = ArrayList()
        timeLine = ArrayList()

        val curTimelines = TimelineAdapter(timeList, mActivity)
        binding?.recTimelines?.layoutManager = LinearLayoutManager(mActivity)
        binding?.recTimelines?.adapter = curTimelines

        val whereTogo = mActivity.whereTogo
        var time = 500  // 5시부터 넣어볼까요~
        val timeDb = db.collection("Reserve").document("${whereTogo.car}M${whereTogo.togo}")

        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("HHmm", Locale.KOREA)
        val curTime = dateFormat.format(date).toInt()
        
        // 현재 시각부터 24시까지
        while( time <= 2400) {
            if (time % 100 == 60) time = time + 100 - 60    // 시간 단위 30분씩
            if (time <= curTime) {
                time += 30
                continue
            }
            val hour = time / 100
            val minute = if (time % 100 == 0) "00" else "30"
            val timeline = "$hour:$minute"
            timeLine.add(timeline)
            //Log.d("TIME_ADD", timeline)
            time += 30
        }
        for(recTime in timeLine){
            timeList.add(Timeline(recTime, 0))
        }
        curTimelines.notifyDataSetChanged()

        // 처음 들어가는게 for문이라서, datachange를 감지하기엔 미묘하다.
        // 필요하다면, 다른 탑승수단의 예약자 수도 표시는 가능한데..
        timeLine.forEachIndexed { index, recTime ->
            timeDb.collection(recTime).document("Info")
                .get().addOnSuccessListener{ snapshot ->
                    if (snapshot.get("count") != null) {
                        val number = snapshot.get("count") as Long
                        Log.d("TIME_SUCC", "[$index] $recTime -> $number")
                        timeList.set( index, Timeline(recTime, number))
                        curTimelines.notifyDataSetChanged()
                    }
                }
        }

        return binding?.root
    }

}