package com.example.taxicar_app

import android.content.Intent
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
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class timelineFragment : Fragment() {
    private var binding: FragmentTimelineBinding? = null
    private val db = Firebase.firestore
    private lateinit var timeList: ArrayList<Timeline>
    private lateinit var timeLine: ArrayList<String>

    // 이거 대신, 시간을 for문이라던가로 돌리고, 그에 맞춰서 9:00 9:30, ""을 추가할것(addonData로 해야할듯)
    val timelines = arrayOf(
        Timeline("9:00", 0),
        Timeline("10:00", 0),
        Timeline("11:00", 0),
        Timeline("12:00", 0),
        Timeline("13:00", 0),
        Timeline("14:00", 0),
        Timeline("15:00", 0),
        Timeline("16:00", 0),
        Timeline("17:00", 0),
        Timeline("18:00", 0),
        Timeline("19:00", 0),
        Timeline("20:00", 0),
        Timeline("21:00", 0),
        Timeline("22:00", 0),
        Timeline("23:00", 0),
        Timeline("24:00", 0),
    )


    // 변수들 진짜.. 이름 비슷해먹었네
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val mActivity = activity as MenuActivity
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
        timeList = ArrayList()
        timeLine = ArrayList()

        val curTimelines = TimelineAdapter(timeList, mActivity)
        binding?.recTimelines?.layoutManager = LinearLayoutManager(mActivity)
        binding?.recTimelines?.adapter = curTimelines

        /*
        예.. 나열을 현재 시간이랑 비교해서,
        1. 현재 시간 < timeline시간 형식으로 보여줍니다. ( 1.timeformat.ToInt 2.for문으로 단순 int )
        2. 각 timeline의 최근 대기방의 사람수를 보여준다.(reseve-wheretogo.car+by-"timelines"-info-count)
            - 다만, 그 시간대가 있는지 확인(!document.isEmpty)
            - 없으면 카운트 0
            - 카운트로 사람 수 표시(지금은 숫자로..?)

         */
        // getTime이 이렇게 되어도 괜찮은가..?
        val whereTogo = mActivity.whereTogo
        var time = 500  // 5시부터 넣어볼까요~
        val timeDb = db.collection("Reserve").document("${whereTogo.car}M${whereTogo.togo}")

        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("HHmm", Locale.KOREA)
        val curTime = dateFormat.format(date).toInt()
        // 대충 gettime부터~ 24시..

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
         * @return A new instance of fragment timelineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            timelineFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}