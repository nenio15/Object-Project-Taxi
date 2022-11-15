package com.example.taxicar_app

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.taxicar_app.databinding.FragmentAlarmBinding
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import kotlin.math.min

/*
//get 날짜
var c: Calendar = Calendar.getInstance()
var year = c.get(Calendar.YEAR)
var month = c.get(Calendar.MONTH)
var day = c.get(Calendar.DAY_OF_MONTH)
//timepicker
private var hour = c.get(Calendar.HOUR_OF_DAY)
private var minute = c.get(Calendar.MINUTE)
 */

class SetAlarm(var year: Int, var month: Int, var day: Int, var hour: Int, var minute: Int)

class AlarmFragment : Fragment() {
    var binding: FragmentAlarmBinding? = null
    private var mActivity: MenuActivity? = null

    fun playSound(){
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(mActivity?.applicationContext, notification)
        ringtone.play() //언제 재생? 조건은?

        //val vibrator = getSystemService(VIBRATE_SERVICE) as Vibrator //진동 (퍼미션 필요)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        mActivity = activity as MenuActivity
        //mActivity = activity as MainActivity // for test
        //초기화 값이 형편없는데요... 지금 시간으로 리셋 못 하려나?
        var alarm = SetAlarm(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE)
        val date: Date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("MM월 dd일")
        val getTime = dateFormat.format(date)
        //현재 날짜. 얘는 날짜 선택용
        binding?.txtSetdate?.text = getTime

        //var dateDialog: DatePickerDialog = DatePickerDialog(mActivity, null, year, month, day)

        //요일 설정
        binding?.txtSetdate?.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    //Log.d("kkang", "${year}년 ${month + 1}월 ${dayOfMonth}일")
                    val setdate: String = "${month + 1}월 ${dayOfMonth}일"
                    binding?.txtSetdate?.text = setdate
                    alarm.year = year
                    alarm.month = month
                    alarm.day = dayOfMonth
                }

            DatePickerDialog(
                activity as MenuActivity,   //얘는 왜...
                dateSetListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        //시간 설정
        binding?.txtSettime?.setOnClickListener {
            var content: String
            val cal = Calendar.getInstance()
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    //Log.d("Time", "${Calendar.MONTH}월 ${Calendar.DAY_OF_MONTH}일")
                    //00시는 내비둘까.. 따로 처리할까, 애초에 헷갈린단 말이지.
                    var t_minute: String = minute.toString()
                    if(minute < 10) t_minute = "0$minute"
                    if( hourOfDay < 12 ) content = "오전 $hourOfDay:$t_minute"
                    else content = "오후 ${hourOfDay - 12}:$t_minute"
                    binding?.txtSettime?.text = content
                    alarm.hour = hourOfDay
                    alarm.minute = minute
                }

            TimePickerDialog(
                mActivity,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
            ).show()
        }

        binding?.btnDecision?.setOnClickListener{
            Toast.makeText(binding?.root?.context, "${alarm.month + 1}월 ${alarm.day}일 ${alarm.hour}:${alarm.minute} 시간으로 예약되었습니다.",
                Toast.LENGTH_SHORT)
                .show()
            //이거 파라매터를... 모르겠네
            mActivity?.addAlarm(alarm.month, alarm.day, alarm.hour, alarm.minute)
            //mActivity?.addAlarm(Calendar.MONTH, Calendar.DAY_OF_MONTH, c_cal.get(Calendar.HOUR_OF_DAY), c_cal.get(Calendar.MINUTE))

            //mActivity?.delAlarm()
        }

        return binding?.root
        }

    companion object {
        @JvmStatic
        fun newInstance() =
            AlarmFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}