package com.example.taxicar_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.taxicar_app.databinding.ActivityMenuBinding
import java.util.*

class MenuActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuBinding
    private lateinit var alarmManager: AlarmManager
    lateinit var whereTogo: Timedata


    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().run {
            replace(binding.frmMenu.id, fragment)
            commit()
        }
    }

    fun goIntent(){
        Log.d("MENU", "go SubActivity... please..")
        val intent = Intent(this, SubActivity::class.java)
        startActivity(intent)
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

    fun navRemote(){
        val navHostFragment = supportFragmentManager.findFragmentById(binding.frmMenu.id) as NavHostFragment
        val navController = navHostFragment.navController
        //setupActionBarWithNavController(navController)
        binding.navBottom.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        Log.d("MENU", item.itemId.toString() + " this is spartaaaaaa")
        if(item.itemId == R.id.action_setting){
            Log.d("menu", "click setting...")
            findNavController(binding.frmMenu.id).navigate(R.id.action_menuFragment_to_action_setting)
            return true
        }
        if(item.itemId == R.id.alarmFragment){
            Log.d("menu, bottom", "click alarm")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        whereTogo = Timedata("", "")


        val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(binding.menuTool.id)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.button_back)
        //mActionBar?.setTitle("메뉴창")
        //mActionBar?.setDisplayHomeAsUpEnabled(true)
        //mActionBar?.setHomeAsUpIndicator(R.drawable.button_back)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.frmMenu.id) as NavHostFragment
        val navController = navHostFragment.navController
        setSupportActionBar(mToolbar)
        //setupActionBarWithNavController(navController) //labeling..
        binding.navBottom.setupWithNavController(navController)
        //binding.menuTool.setupWithNavController(navController)
        //appBarConfiguration =



        setContentView(binding.root)
    }
}