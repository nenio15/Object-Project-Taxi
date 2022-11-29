package com.example.taxicar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
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

    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance().reference

        // 초기화
        messageList = ArrayList()
        val messageAdapter = MessageAdapter(this, messageList)
        binding.recMessages.layoutManager = LinearLayoutManager(this)
        binding.recMessages.adapter = messageAdapter

        //넘어온 데이터 변수에 담기
        //curName = intent.getStringExtra("name").toString()  // 메뉴에서 주는 것보다, 여기서 받는게 빠르다.(서버가 있어서 그런듯..)
        //curUid = intent.getStringExtra("uid").toString()
        curUid = auth.currentUser?.uid!!        //있는거라서 intent로 안 받음.

        val curChatroom = intent.getStringExtra("chatroom") as String
        val curRoomid = intent.getStringExtra("roomid") as String
        /*
        val time = intent.getStringExtra("reserveTime").toString().split(":")
        val car = intent.getStringExtra("by").toString()
        val togo = intent.getStringExtra("togo").toString()
        val curChatroom = "${car}M${togo}M${time[0]}${time[1]}"
         */

        val curRoominfo = curChatroom.split('M')
        val time = if((curRoominfo[2].toInt() / 100) < 10) "${curRoominfo[2][0]}:${curRoominfo[2].substring(1,2)}"
            else "${curRoominfo[2].substring(0, 1)}:${curRoominfo[2].substring(2)}"
        binding.destination.text = "${curRoominfo[0]} ${curRoominfo[1]}"
        binding.resTime.text = "예약 $time"

        // 닉네임 찾기..
        Firebase.firestore.collection("Nicknames")
            .document(auth.currentUser?.uid!!)
            .get().addOnSuccessListener { snapshot ->
                if( snapshot == null ) return@addOnSuccessListener
                curName = snapshot.get("nickname").toString()
                Log.d("CHAT_NAME", curName)
            }

        //supportActionBar?.title = receiveName   // this is working?

        // 지금 방 구분이 시간뿐임 ( 탑승, 목적지 추가..)
        binding.sendBtn.setOnClickListener{
            val message = binding.msgEdit.text.toString()
            val messageObject = Message(message, auth.currentUser?.displayName, markCurTime())  // curName

            // 예약시간의 방으로 메세지 add
            db.child("chats").child(curChatroom).child(curRoomid).child("messages")
                .push().setValue(messageObject)//.addOnSuccessListener

            binding.msgEdit.setText("")
        }

        // 단톡방 나가기
        binding.btnTest3.setOnClickListener{ finish() }

        // 단톡방에 메세지 갱신
        db.child("chats").child(curChatroom).child(curRoomid).child("messages")
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

        // 채팅방 메뉴 목록
        binding.chatMenu.setOnClickListener {
            drawer.openDrawer(GravityCompat.END)
        }

        // 공지 firebase로 ㄱㄱ
        binding.navNotification.setOnClickListener {
            drawer.closeDrawer(GravityCompat.END)
            val dialog = CustomDialog(this)
            dialog.showDialog()
            dialog.setOnClickListener(object: CustomDialog.OnDialogClickListener{
                override fun onClicked(notice: String) {
                    val long_notice = notice
                    // val long_notice = binding.noticeOnly.text.toString()

                    // 데이터 setting
                    val notification = Notice(long_notice)

                    // 데이터베이스에 등록
                    db.child("chats").child(curChatroom).child(curRoomid).child("notice")
                        .push().setValue(notification)
                        .addOnSuccessListener {
                        }
                }

            })

        }
        // 공지를 firebase에서 가져와야하는데
//        adapter = NoticeAdapter(this, noticeList)
        // recyclerView 초기화
        db.child("chats").child(curChatroom).child(curRoomid).child("notice")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val notice = postSnapshat.getValue(Notice::class.java)
                        binding.noticeOnly.text = notice?.notification
                        // 삭제
                    }
                    // 공지 빙글 뱅글
                    binding.noticeOnly.isSelected = true


                }
                override fun onCancelled(error: DatabaseError) {
                }
            })


        binding.navNotificationDelete.setOnClickListener {
            db.child("chats").child(curChatroom).child(curRoomid).child("notice").removeValue()
            //binding.noticeOnly.text = null
            binding.noticeOnly.text = ""
            drawer.closeDrawer(GravityCompat.END)
        }

        // 값이 날아가지 않으려면 어떻게 해야 할까나..
        binding.navChatOut.setOnClickListener {
            drawer.closeDrawer(GravityCompat.END)
            finish()
        }

        //기본 양식은 아래와 같다. 자세한 것은 firebase주소를 직접 찾아가 볼것.
        //mDatabaseR.child("room").child("taxi").child("toSchool").child("room1")

        setContentView(binding.root)
    }

    fun markCurTime(): String{
        val date = Date(System.currentTimeMillis()) //현재시각
        val dateFormat = SimpleDateFormat("HH :mm", Locale.KOREA)
        dateFormat.timeZone = TimeZone.getTimeZone("Asiz/Seoul")
        val nowTime = dateFormat.format(date).split(" ")
        var baTime: String
        if(nowTime[0].toInt() < 12) baTime = "오전 " + nowTime[0].toInt().toString() + nowTime[1]
        else baTime = "오후 " + (nowTime[0].toInt() - 12).toString() + nowTime[1]

        //Log.d("CHAT", baTime)
        return baTime
    }

}