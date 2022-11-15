package com.example.taxicar_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.ActivityMainBinding
import android.util.Log
import android.view.View
import java.util.*


class MainActivity : AppCompatActivity() {


    var destination: String? = null
    //private var alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    lateinit var binding : ActivityMainBinding

    //화면 간이 전환
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().run {
            replace(binding.frmUi.id, fragment)
            commit()
        }
    }

    /*
    fun showRecTime(index1: Int, index2: Int){    // 파라미터는 목적지랑 탑승수단.
        //다른거 view도 조정을..?
        //Log.d("MAIN", "show rectime")
        binding.frmUi.visibility = View.GONE
        binding.recTimelines.visibility = View.VISIBLE
        //binding.listBack.visibility = View.VISIBLE

        binding.recTimelines.layoutManager = LinearLayoutManager(this)
        binding.recTimelines.adapter = TimelineAdapter(timelines)
    }

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MainFragment.newInstance())
        //val TAG: String = "로그"
        //Log.d(TAG, "call main")

        /*
        binding.btnTest.setOnClickListener{
            //list있을때 활성화입니다. 아마,
            //근데 new아니지 않아요?
            //replaceFragment(fragment_choice.newInstance())
            Log.d("MAIN", "delete rectime")
            binding.frmUi.visibility = View.VISIBLE
            //binding.listBack.visibility = View.INVISIBLE
            binding.recTimelines.visibility = View.GONE
        }

         */

        //다른 fragment에서 입력이 가능한가요..?
        //이것들 view가 on/off로 보이게끔 하는것도 찾아야하는데 말이죠. 이것만 해도... 하....
        //
    }

}