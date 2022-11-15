package com.example.taxicar_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.ActivityMainBinding
import com.example.taxicar_app.databinding.ActivityMenuBinding
import java.util.*

class MenuActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuBinding
    private lateinit var alarmManager: AlarmManager

    val timelines = arrayOf(
        Timeline("9:00", "홍길동 김철수 김영희"),
        Timeline("10:00", ""),
        Timeline("11:00", ""),
        Timeline("12:00", ""),
        Timeline("13:00", ""),
        Timeline("14:00", "홍길동"),
        Timeline("15:00", ""),
        Timeline("16:00", ""),
        Timeline("17:00", ""),
        Timeline("18:00", ""),
        Timeline("19:00", ""),
        Timeline("20:00", ""),
        Timeline("21:00", ""),
        Timeline("22:00", ""),
        Timeline("23:00", ""),
        Timeline("24:00", ""),
    )

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().run {
            replace(binding.frmMenu.id, fragment)
            commit()
        }
    }

    fun showRecTime(index1: Int, index2: Int){    // 파라미터는 목적지랑 탑승수단.
        //다른거 view도 조정을..?
        //Log.d("MAIN", "show rectime")
        binding.frmMenu.visibility = View.GONE
        binding.recTimelines.visibility = View.VISIBLE
        //binding.listBack.visibility = View.VISIBLE

        binding.recTimelines.layoutManager = LinearLayoutManager(this)
        binding.recTimelines.adapter = TimelineAdapter(timelines)
    }

    fun addAlarm(month: Int, day: Int, hour: Int, minute: Int){
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //intent가 안됨, 방법 찾아볼것
        val intent = Intent(this, AlarmRecevier::class.java)
        val pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        //PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2022)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pIntent)
    }

    fun delAlarm(){
        val intent = Intent(this, AlarmRecevier::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MenuFragment.newInstance())

        binding.btnAlarm.setOnClickListener{
            replaceFragment(AlarmFragment.newInstance())
        }
        binding.btnHome.setOnClickListener{
            replaceFragment(MenuFragment.newInstance())
        }
        binding.btnDest.setOnClickListener{
            replaceFragment(ChoiceFragment.newInstance())
        }

        binding.btnTest2.setOnClickListener{
            //list있을때 활성화입니다. 아마,
            //근데 new아니지 않아요?
            //replaceFragment(fragment_choice.newInstance())
            Log.d("MAIN", "delete rectime")
            binding.frmMenu.visibility = View.VISIBLE
            //binding.listBack.visibility = View.INVISIBLE
            binding.recTimelines.visibility = View.GONE
        }

    }
}