package com.example.taxicar_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService


class AlarmRecevier: BroadcastReceiver(){

    public class AlarmRecevier(){ }

    lateinit var manager: NotificationManager
    var builder: NotificationCompat.Builder? = null

    private val CHANNEL_ID = "channel1"
    private val CHANNEL_NAME = "Channel1"

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            Log.e("AlarmReceiver", System.currentTimeMillis().toString())
        }
        val alarmsound = RingtoneManager.TYPE_ALARM
        val am = context?.getSystemService(Context.ALARM_SERVICE)
        manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            )
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
        }else{
            builder = NotificationCompat.Builder(context, CHANNEL_ID) // only context?
            Log.d("AlarmReceiver", "빌더 else네요 오류 없음?")
        }

        val intent2 = Intent(context, MenuActivity::class.java)
        // maybe error for multibly..?
        val pendingIntent = PendingIntent.getActivity(context, 101, intent2, PendingIntent.FLAG_IMMUTABLE)

        //알림창 제목
        builder?.setContentTitle("알람")
        builder?.setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
        builder?.setAutoCancel(true)
        builder?.setContentIntent(pendingIntent)
        builder?.setSubText("알람이 울렸어요....")
        builder?.setSound(RingtoneManager.getDefaultUri(alarmsound))
        //AlarmFragment.pl(RingtoneManager.TYPE_NOTIFICATION)

        val notification = builder?.build()
        manager.notify(1, notification)
        Log.d("AlarmReceiver", "알람 푸쉬 빌드 성공")

    }
}

