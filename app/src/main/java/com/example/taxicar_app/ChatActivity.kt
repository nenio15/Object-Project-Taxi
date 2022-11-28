package com.example.taxicar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.list_chatroom.*
import kotlinx.android.synthetic.main.notification_edit.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityChatBinding
    private var mFirebaseD: FirebaseDatabase = FirebaseDatabase.getInstance()
    //아래 문장이 realtime base를 get하는것. 원리는 json의 key값을 받는 형식과 같다.
    private var mDatabaseR: DatabaseReference = mFirebaseD.getReference()


    lateinit var noticeList: kotlin.collections.ArrayList<Notice>
    // 공지 데이터베이스 클래스 객체 생서
//    val noticeDb = NoticeAdapter()

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
        val car = intent.getStringExtra("by").toString()
        val togo = intent.getStringExtra("togo").toString()
        val curChatroom = "${car}M${togo}M${time[0]}${time[1]}"


        binding.destination.text = "${car} ${togo}"
        binding.resTime.text = "예약시간 ${time[0]}:${time[1]}"

        // maybe.. need to conver to merge in one room
        senderRoom = curUid

       // supportActionBar?.title = curName   // Is this working?

        // 지금 방 구분이 시간뿐임 ( 탑승, 목적지 추가..)
        binding.sendBtn.setOnClickListener{
            val message = binding.msgEdit.text.toString()
            val date = Date(System.currentTimeMillis()) //현재시각
            val dateFormat = SimpleDateFormat("HH :mm", Locale.KOREA)
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val nowTime = dateFormat.format(date).split(" ")
            var baTime: String
            if(nowTime[0].toInt() < 12) baTime = "오전" + nowTime[0].toInt().toString() + nowTime[1]
            else baTime = "오후" + (nowTime[0].toInt() - 12).toString() + nowTime[1]
            Log.d("CHAT", baTime)
            val messageObject = Message(message, curUid, baTime)  // curName

            // 예약시간의 방으로 add
            db.child("chats").child(curChatroom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //delete receiverRoom.
                    //Log.d("CHAT", "add Successful")
                    //db.child("chats").child(receiverRoom).child("messages").push()
                    //    .setValue(messageObject)
                }
            binding.msgEdit.setText("")
        }


        binding.btnTest3.setOnClickListener{ finish() }

        db.child("chats").child(curChatroom).child("messages")
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
                    db.child("chats").child(curChatroom).child("notice").push().setValue(notification)
                        .addOnSuccessListener {
                    }
                }

            })

        }
        // 공지를 firebase에서 가져와야하는데
//        adapter = NoticeAdapter(this, noticeList)
        // recyclerView 초기화
        db.child("chats").child(curChatroom).child("notice")
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
            db.child("chats").child(curChatroom).child("notice").removeValue()
            //binding.noticeOnly.text = null
            binding.noticeOnly.text = ""
            drawer.closeDrawer(GravityCompat.END)
        }

        // 값이 날아가지 않으려면 어떻게 해야 할까나..
        binding.navChatOut.setOnClickListener {
            drawer.closeDrawer(GravityCompat.END)
            finish()
        }

        setContentView(binding.root)

    } // onCreate
}