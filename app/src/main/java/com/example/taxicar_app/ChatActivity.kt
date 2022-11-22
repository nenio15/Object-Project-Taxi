package com.example.taxicar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityChatBinding
    private var mFirebaseD: FirebaseDatabase = FirebaseDatabase.getInstance()
    //아래 문장이 realtime base를 get하는것. 원리는 json의 key값을 받는 형식과 같다.
    private var mDatabaseR: DatabaseReference = mFirebaseD.getReference()

    private lateinit var curName: String
    private lateinit var curUid: String
    lateinit var db: DatabaseReference
    private lateinit var curRoom: String
    private lateinit var senderRoom: String

    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance().reference

        // 초기화
        messageList = ArrayList()
        val messageAdapter = MessageAdapter(this, messageList)
        // RecyclerView
        binding.recMessages.layoutManager = LinearLayoutManager(this)
        binding.recMessages.adapter = messageAdapter

        //넘어온 데이터 변수에 담기
        curName = intent.getStringExtra("name").toString()
        curUid = intent.getStringExtra("uid").toString()
        val time = intent.getStringExtra("reserveTime").toString().split(":")
        val reserveTime = time[0] + time[1]

        // maybe.. need to conver to merge in one room
        senderRoom = curUid

        //supportActionBar?.title = receiveName   // this is working?

        // 지금 방 구분이 시간뿐임 ( 탑승, 목적지 추가..)
        binding.sendBtn.setOnClickListener{
            val message = binding.msgEdit.text.toString()
            val date = Date(System.currentTimeMillis()) //현재시각
            val dateFormat = SimpleDateFormat("HH :mm", Locale.KOREA)
            dateFormat.timeZone = TimeZone.getTimeZone("Asiz/Seoul")
            val nowTime = dateFormat.format(date).split(" ")
            var baTime: String
            if(nowTime[0].toInt() < 12) baTime = "오전 " + nowTime[0].toInt().toString() + nowTime[1]
            else baTime = "오후 " + (nowTime[0].toInt() - 12).toString() + nowTime[1]
            Log.d("CHAT", baTime)
            val messageObject = Message(message, curUid, baTime)  // curName

            // 예약시간의 방으로 add
            db.child("chats").child(reserveTime).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //delete receiverRoom.
                    //Log.d("CHAT", "add Successful")
                    //db.child("chats").child(receiverRoom).child("messages").push()
                    //    .setValue(messageObject)
                }
            binding.msgEdit.setText("")
        }

        binding.btnTest3.setOnClickListener{ finish() }

        db.child("chats").child(reserveTime).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for(postSnapshat in snapshot.children){
                        val message = postSnapshat.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        //기본 양식은 아래와 같다. 자세한 것은 firebase주소를 직접 찾아가 볼것.
        //mDatabaseR.child("room").child("taxi").child("toSchool").child("room1")

        setContentView(binding.root)
    }
}