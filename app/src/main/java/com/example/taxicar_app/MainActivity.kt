package com.example.taxicar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.ActivityMainBinding
import android.util.Log
import android.view.View


class MainActivity : AppCompatActivity() {

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
    var destination: String? = null


    lateinit var binding : ActivityMainBinding

    //화면 간이 전환
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().run {
            replace(binding.frmUi.id, fragment)
            commit()
        }
    }

    fun showRecTime(index1: Int, index2: Int){    // 파라미터는 목적지랑 탑승수단.
        //다른거 view도 조정을..?
        //Log.d("MAIN", "show rectime")
        binding.frmUi.visibility = View.GONE
        binding.listBack.visibility = View.VISIBLE
        binding.recTimelines.visibility = View.VISIBLE

        binding.recTimelines.layoutManager = LinearLayoutManager(this)
        binding.recTimelines.adapter = TimelineAdapter(timelines)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MainFragment.newInstance())
        //val TAG: String = "로그"
        //Log.d(TAG, "call main")

        binding.btnTest.setOnClickListener{
            //list있을때 활성화입니다. 아마,
            //근데 new아니지 않아요?
            //replaceFragment(fragment_choice.newInstance())
            Log.d("MAIN", "delete rectime")
            binding.frmUi.visibility = View.VISIBLE
            binding.listBack.visibility = View.INVISIBLE
            binding.recTimelines.visibility = View.GONE
        }

        //다른 fragment에서 입력이 가능한가요..?
        //이것들 view가 on/off로 보이게끔 하는것도 찾아야하는데 말이죠. 이것만 해도... 하....
        //
    }
}