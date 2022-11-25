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

class MenuActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMenuBinding
    private lateinit var alarmManager: AlarmManager
    lateinit var whereTogo: Timedata
    private val db = Firebase.firestore
    private var room = "roomM0"
    private var cnt: Long = 0

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().run {
            replace(binding.frmMenu.id, fragment)
            commit()
        }
    }

    fun goIntent(reserveTime: String) {
        val intent = Intent(this, ChatActivity::class.java)
        val reserveDb = db.collection("Reserve")
            .document("${whereTogo.car}M${whereTogo.togo}")

        //필수.. 보내기
        val time = reserveTime.split(":")
        val curChatroom = "${whereTogo.car}M${whereTogo.togo}M${time[0]}${time[1]}"
        intent.putExtra("chatroom", curChatroom)
        val rdb = FirebaseDatabase.getInstance().reference

        // 원래는... 있으면 그냥 보내주는거야, intent로..
        // 근데 머가 많이 꼬였어.. 조건문이 망했어
        // uid가 해당 시간대에 있으면 그냥 보내주는 거라고!!!
        //      조건 1.해당 유저의 uid가 존재함
        //      조건 2.방id의 count가 4가 넘음(count의 갱신 필요) (반대로 cnt가 0이면, 방 새로 팔것)
        // 문제 1- nickname이 갱신이 아니라, 그냥 세팅으로 됨(추가가 안됨..)(이건 필드값만 바꾸면 될..듯?

        // 해당 유저의 uid존재 여부
        reserveDb.collection(reserveTime).whereEqualTo("uid", auth.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents)
                        room = document.get("roomid").toString()
                    Log.d("MENU_I", "please start at the end...")
                    intent.putExtra("roomid", room)
                    startActivity(intent)
                } else {
                    // 예약 시간대가 처음인지 ask
                    reserveDb.collection(reserveTime).document("Info").get()
                        .addOnSuccessListener { snapshot ->
                            if (snapshot != null) {
                                val zero: Long = 0
                                // 정말 더러운 조건문이군..
                                if(snapshot.get("curRoomid") != null) room = snapshot.get("curRoomid") as String
                                if(snapshot.get("count") != null) cnt = snapshot.get("count") as Long
                                Log.d("MENU_GET", "$room and $cnt")

                                if (cnt >= 4) {
                                    cnt = 0
                                    room = "roomM" + (room.split('M')[1].toInt() + 1)
                                } else if (cnt == zero) {
                                    rdb.child("chats").child(curChatroom).child(room).child("createdTime")
                                        .setValue(hashMapOf("created" to getTime()))
                                }

                            } else {    // info가 존재치 않는경우
                                rdb.child("chats").child(curChatroom).child(room).child("createdTime")
                                    .setValue(hashMapOf("created" to getTime()))

                                Log.d("MENU_ADD_INFO", "no info.. so creat one")
                            }

                            //여기 room30넘으면 리셋도 필요함..
                            addNew(reserveDb, rdb, reserveTime, curChatroom)
                            intent.putExtra("roomid", room)
                            startActivity(intent)


                        }
                        .addOnFailureListener { // first room open 처음이에요..!

                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("MENU_failed", "no uid so.. need to.....", exception)

            }

/*
        val exist = checkUidInR(reserveDb, reserveTime)
        if (!exist) { // uid 존재안함..
            Log.d("MENU", "yes!!!! no uid, we need to add")
            UpdateInfo(reserveDb, rdb, reserveTime, curChatroom)

            val baseData = hashMapOf(
                "uid" to auth.currentUser?.uid,
                "roomid" to room,    // register's name // need? collapse
                "time" to getTime() // reserve timeline 어차피 얼마없..?
                // "day" to         // 이걸로 요일 구분... 이건 언제 또 쓰냐?
            )
            val info = listOf(whereTogo.togo, whereTogo.car, room)
            // TODO 아마 서치가 안될거임...(메뉴창에서) 어쩌냐,,,
            val infoData = hashMapOf(curChatroom to info)   //"nickname" to auth.currentUser?.displayName,

            Log.d("MENU_addroom", room)

            // 예약 시간대 추가
            reserveDb.collection(reserveTime)
                .add(baseData)  //.add(data)는 보류, 더미가 쌓임(근데.. 이미 있으면, room넘버링이 바뀌는데?
                .addOnSuccessListener {
                    Toast.makeText(
                        binding.root.context, "${reserveTime} 시간대로 설정되었습니다.",
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

            // 닉네임 info에 방 정보 넣기
            db.collection("Nicknames").document(auth.currentUser?.uid!!).set(infoData, SetOptions.merge()) // 없어지는지 확인
            // 단톡방 user에 uid 추가하기(4명이 정원)
            rdb.child("chats").child(curChatroom).child(room).child("users")
                .push()
                .setValue(hashMapOf("uid" to auth.currentUser?.uid!!))  //push를 시켜야 랜덤id에 넣을 수 있다리..

        }

 */

        //Log.d("MENU", "null? i dont know")
        /*
        // 예약 시간대 설정
        db.collection("Reserve")
            .document(whereTogo.car)
            .collection(whereTogo.togo)
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(binding.root.context, "${reserveTime} 시간대로 설정되었습니다.",
                    Toast.LENGTH_SHORT)
                    .show()
                Log.d("Timeline", "Document added: $it")
            }
            .addOnFailureListener{ e ->
                Toast.makeText(binding.root.context, "전송실패..", Toast.LENGTH_SHORT).show()
                Log.d("Timeline", "Error occcurs: $e")
            }

         */
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

    // useless, didn't use
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
        auth = Firebase.auth
        whereTogo = Timedata("", "")


        val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(binding.menuTool.id)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.button_back)
        //mActionBar?.setTitle("메뉴창")
        //mActionBar?.setDisplayHomeAsUpEnabled(true)
        //mActionBar?.setHomeAsUpIndicator(R.drawable.button_back)

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

    private fun checkUidInR(db: DocumentReference, rTime: String): Boolean{
        var exist = false
        db.collection(rTime).whereEqualTo("uid", auth.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { documents ->
                for ( document in documents ){
                    Log.d("MENU_success", "${document.id} => ${document.data}")
                }
                exist = true
            }
            .addOnFailureListener { exception ->
                Log.w("MENU_failed", exception)
            }

        return exist
    }

    private fun UpdateInfo(db: DocumentReference, rdb: DatabaseReference, rTime: String, chatroom: String){
        db.collection(rTime).document("Info").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.get("curRoomid") == null || snapshot.get("count") == null) return@addOnSuccessListener
                room = snapshot.get("curRoomid") as String
                cnt = snapshot.get("count") as Long
                Log.d("MENU_GET", "$room and $cnt")

                //여기 room30넘으면 리셋도 필요함..
                if (cnt >= 4) {
                    cnt = 0
                    room = "roomM" + (room.split('M')[1].toInt() + 1)
                } else if (cnt.equals(0)) {
                    rdb.child("chats").child(chatroom).child(room).child("createdTime")
                        .setValue(hashMapOf("created" to getTime()))
                }
            }
            .addOnFailureListener { // first room open
                rdb.child("chats").child(chatroom).child(room).child("createdTime")
                    .setValue(hashMapOf("created" to getTime()))

                Log.d("MENU_ADD_INFO", "no info.. so creat one")
            }

        val roomData = hashMapOf(
            "curRoomid" to room,    // roomM0
            "count" to ++cnt        // 1
        )
        Log.d("MENU_MOVE", cnt.toString())
        db.collection(rTime).document("Info").set(roomData)

    }

    private fun addNew(ndb: DocumentReference, rdb: DatabaseReference, rTime: String, chatroom: String){
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
        val info = listOf(whereTogo.togo, whereTogo.car, room)
        // TODO 아마 서치가 안될거임...(메뉴창에서) 어쩌냐,,,
        val infoData = hashMapOf(chatroom to info)   //"nickname" to auth.currentUser?.displayName,

        Log.d("MENU_addroom", room)

        // 예약 시간대 추가
        ndb.collection(rTime)
            .add(baseData)  //.add(data)는 보류, 더미가 쌓임(근데.. 이미 있으면, room넘버링이 바뀌는데?
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

        // 닉네임 info에 방 정보 넣기 (이것만큼은 되는구나..)
        db.collection("Nicknames").document(auth.currentUser?.uid!!).set(infoData, SetOptions.merge())
        // 단톡방 user에 uid 추가하기(4명이 정원)
        rdb.child("chats").child(chatroom).child(room).child("users")
            .push()
            .setValue(hashMapOf("uid" to auth.currentUser?.uid!!))

    }

}