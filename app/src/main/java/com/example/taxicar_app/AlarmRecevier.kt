package com.example.taxicar_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AlarmRecevier: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            Log.e("알람", System.currentTimeMillis().toString())
        }
    }
}
