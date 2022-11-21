package com.example.taxicar_app

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
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
import androidx.core.content.getSystemService
import com.example.taxicar_app.databinding.FragmentAlarmBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
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

class SetAlarm(var year: Int, var month: Int, var day: Int, var hour: Int, var minute: Int){

}

class AlarmFragment : Fragment() {
    var binding: FragmentAlarmBinding? = null
    private var mActivity: MenuActivity? = null
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager


    fun playSound(type: Int){
        val notification: Uri = RingtoneManager.getDefaultUri(type)
        val ringtone = RingtoneManager.getRingtone(mActivity?.applicationContext, notification)
        ringtone.play() //언제 재생? 조건은?
        Log.d("Alarm", "play sound")
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
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        mActivity = activity as MenuActivity
        //초기화 값이 형편없는데요... 지금 시간으로 리셋 못 하려나?

        val date = Date(System.currentTimeMillis()) //현재시각
        val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH.mm", Locale.KOREA)
        val nowTime = dateFormat.format(date)
        val curTime = nowTime.split('.') //year, month, day, hour, minute
        //var alarm = SetAlarm(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE)

        //진짜 코드 더럽다..
        var alarm = SetAlarm(curTime[0].toInt(), curTime[1].toInt(), curTime[2].toInt(), 0, 0) //curTime[3].toInt(), curTime[4].toInt())
        Log.d("Alarm", "${nowTime}")
        //현재 날짜. 얘는 날짜 선택용
        ////////
        val mCalender = GregorianCalendar()
        Log.d("G-Alarm", mCalender.time.toString())

        binding?.txtSetdate?.text = "${curTime[1]}월 ${curTime[2]}일"


        //val sf = SimpleDateFormat("yy/MM/dd HH:mm", Locale.KOREA)
        //sf.timeZone = TimeZone.getTimeZone("Asiz/Seoul")

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
            //AlarmReceiver에 값 전달
            val receiverIntent = Intent(mActivity, AlarmRecevier::class.java)
            val pendingIntent = PendingIntent.getBroadcast(mActivity, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE)

            /*
            val from = "2022-11-21 16:40:00"

            //val nowTime = dateFormat.format(date)
            val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            var datetime: Date? = null
            try{
                datetime = dateFormat.parse(from)
            } catch (e: ParseException){
                e.printStackTrace()
            }
            //val datel = dateFormat.parse(datestr)
             */


            val calendar = Calendar.getInstance()
            //calendar.time = datetime!!        //그냥 dateformat임. 생겨먹은건 상관없는듯?

            alarmManager.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
            //mActivity?.addAlarm(Calendar.MONTH, Calendar.DAY_OF_MONTH, c_cal.get(Calendar.HOUR_OF_DAY), c_cal.get(Calendar.MINUTE))
            playSound(RingtoneManager.TYPE_NOTIFICATION)
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