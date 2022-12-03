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

        val date = Date(System.currentTimeMillis()) //현재시각
        val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH.mm", Locale.KOREA)
        dateFormat.timeZone = TimeZone.getTimeZone("Asiz/Seoul")
        val nowTime = dateFormat.format(date)
        val curTime = nowTime.split('.') //year, month, day, hour, minute

        var alarm = SetAlarm(curTime[0].toInt(), curTime[1].toInt() - 1, curTime[2].toInt(), 0, 0)
        Log.d("Alarm", "${alarm.year} ${alarm.month} ${alarm.day}")

        val mCalender = GregorianCalendar()
        Log.d("G-Alarm", mCalender.time.toString())

        binding?.txtSetdate?.text = "${curTime[1]}월 ${curTime[2]}일"

        //요일 설정
        binding?.txtSetdate?.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val setdate: String = "${month + 1}월 ${dayOfMonth}일"
                    binding?.txtSetdate?.text = setdate
                    alarm.year = year
                    alarm.month = month
                    alarm.day = dayOfMonth
                    Log.d("ALARM_DAY", "${alarm.year} ${alarm.month} ${alarm.day} new changed")
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

            val calendar = mActivity?.addAlarm(alarm)

            //AlarmReceiver에 값 전달
            val receiverIntent = Intent(mActivity, AlarmRecevier::class.java)
            val pendingIntent = PendingIntent.getBroadcast(mActivity, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE)

            alarmManager.set(AlarmManager.RTC, calendar!!.timeInMillis, pendingIntent)
            playSound(RingtoneManager.TYPE_NOTIFICATION)
        }

        return binding?.root
        }

}