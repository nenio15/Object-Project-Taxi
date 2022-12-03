package com.example.taxicar_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.taxicar_app.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MenuActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMenuBinding
    private lateinit var alarmManager: AlarmManager
    lateinit var whereTogo: Timedata
    private val db = Firebase.firestore
    private var room = "roomM0"
    private var cnt: Long = 0
    
    fun goIntent(reserveTime: String) {
        val intent = Intent(this, ChatActivity::class.java)
        val reserveDb = db.collection("Reserve")
            .document("${whereTogo.car}M${whereTogo.togo}")

        //채팅 activity로 보낼 것
        val time = reserveTime.split(":")
        val curChatroom = "${whereTogo.car}M${whereTogo.togo}M${time[0]}${time[1]}"
        val rdb = FirebaseDatabase.getInstance().reference
        intent.putExtra("chatroom", curChatroom)

        // 시간대를 통한 단톡방 접속
        reserveDb.collection(reserveTime).whereEqualTo("uid", auth.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { documents ->
                // 해당 유저의 uid존재 여부
                if (!documents.isEmpty) {
                    for (document in documents)
                        room = document.get("roomid").toString()
                    Log.d("MENU_connect", "uid already exist.. just go to room")
                    intent.putExtra("roomid", room)
                    startActivity(intent)
                } else {
                    // 예약 시간대가 처음인지 ask
                    reserveDb.collection(reserveTime).document("Info").get()
                        .addOnSuccessListener { snapshot ->
                            if (snapshot != null) {
                                val zero: Long = 0
                                if(snapshot.get("curRoomid") != null) room = snapshot.get("curRoomid") as String    // room = snapshot.get() ?: "roomM0" ( ?: is ... well?)
                                if(snapshot.get("count") != null) cnt = snapshot.get("count") as Long
                                Log.d("MENU_GET", "$room and $cnt")

                                // 해당 단톡방 인원수 초과
                                if (cnt >= 4) {
                                    cnt = 0
                                    room = "roomM" + (room.split('M')[1].toInt() + 1)
                                } else if (cnt == zero) {
                                    rdb.child("chats").child(curChatroom).child(room).child("createdTime")
                                        .setValue(hashMapOf("created" to getTime()))
                                }

                            } else {    // info가 존재치 않는경우 ( 해당 시간대의 첫 번째 대기자 )
                                rdb.child("chats").child(curChatroom).child(room).child("createdTime")
                                    .setValue(hashMapOf("created" to getTime()))

                                Log.d("MENU_ADD_INFO", "no info.. so creat one")
                            }

                            //여기 room30넘으면? 리셋, 아니면 방 제한은 나중에(0리셋이 오히려 버그날듯)
                            addNew(reserveDb, rdb, reserveTime, curChatroom)
                            intent.putExtra("roomid", room)
                            startActivity(intent)

                        }
                }
            }

    }

    fun addAlarm(alarm: SetAlarm): Calendar{        //year: Int, month: Int, day: Int, hour: Int, minute: Int): Calendar{
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //intent가 안됨, 방법 찾아볼것
        val intent = Intent(this, AlarmRecevier::class.java)
        val pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val cal = Calendar.getInstance()

        cal.set(alarm.year, alarm.month, alarm.day, alarm.hour, alarm.minute, 0)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pIntent)

        return cal
    }

    /*
    fun delAlarm(){
        val intent = Intent(this, AlarmRecevier::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
    */

    /*
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
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        auth = Firebase.auth
        whereTogo = Timedata("", "")

        val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(binding.menuTool.id)
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.button_back)  // this doesn't work

        val navHostFragment = supportFragmentManager.findFragmentById(binding.frmMenu.id) as NavHostFragment
        val navController = navHostFragment.navController
        setSupportActionBar(mToolbar)
        binding.navBottom.setupWithNavController(navController)
        binding.menuTool.setupWithNavController(navController)

        binding.btnSetting.setOnClickListener{
            findNavController(binding.frmMenu.id).navigate(R.id.settingFragment)
        }

        setContentView(binding.root)
    }

    private fun getTime(): String{
        val date = Date(System.currentTimeMillis()) //현재시각
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.KOREA)
        dateFormat.timeZone = TimeZone.getTimeZone("Asiz/Seoul")
        return dateFormat.format(date)
    }

    // TODO 함수명, 변수명 정리(난잡함..)
    private fun addNew(ndb: DocumentReference, rdb: DatabaseReference, rTime: String, chatroom: String){
        val uidDb = db.collection("Nicknames").document(auth.currentUser?.uid!!)

        // 이것들 다 data class로 변환시킬것.
        val roomData = hashMapOf(
            "curRoomid" to room,    // roomM0
            "count" to ++cnt        // 1
        )
        Log.d("MENU_MOVE", cnt.toString())
        ndb.collection(rTime).document("Info").set(roomData)

        val baseData = hashMapOf(
            "uid" to auth.currentUser?.uid,
            "roomid" to room,    // register's name // need? collapse
            "time" to getTime() // reserve timeline 어차피 얼마없..?
            // "day" to         // 이걸로 요일 구분... 이건 언제 또 쓰냐?
        )
        val info = listOf(whereTogo.togo, whereTogo.car, rTime, room)
        val infoData = hashMapOf(chatroom to info)
        Log.d("MENU_addroom", room)

        // 예약 시간대 추가
        ndb.collection(rTime)
            .add(baseData)
            .addOnSuccessListener {
                Toast.makeText(
                    binding.root.context, "${rTime} 시간대로 설정되었습니다.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("Timeline", "Document added: $it")
            }
            .addOnFailureListener { e ->
                Toast.makeText(binding.root.context, "전송실패..", Toast.LENGTH_SHORT)
                    .show()
                Log.d("Timeline", "Error occcurs: $e")
            }

        uidDb.set(infoData, SetOptions.merge())
        uidDb.get().addOnSuccessListener { snapshot ->
            if(snapshot.get("roomlist") != null) {
                val roomlist = snapshot.get("roomlist") as ArrayList<String>
                // 새로 추가되는 요소가 0번째로 가도록 (역정렬)
                val roomExtense = arrayListOf(chatroom).plus(roomlist)
                Log.d("ROOM_MENU_LIST", roomExtense.toString())
                uidDb.set(hashMapOf("roomlist" to roomExtense), SetOptions.merge())
            }else {
                Log.d("ROOM_MENU_LIST_NULL", chatroom)
                uidDb.set(hashMapOf("roomlist" to listOf(chatroom)), SetOptions.merge())
            }
        }

        // 단톡방 user에 uid 추가하기(4명이 정원)
        rdb.child("chats").child(chatroom).child(room).child("users")
            .push()
            .setValue(hashMapOf("uid" to auth.currentUser?.uid!!))

    }
}