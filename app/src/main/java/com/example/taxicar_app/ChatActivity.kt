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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityChatBinding
    private lateinit var curName: String
    private lateinit var curUid: String
    lateinit var db: DatabaseReference
    private val storeDb = Firebase.firestore

    private lateinit var messageList: ArrayList<Message>
    private lateinit var memberList: ArrayList<Member>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance().reference

        // 초기화
        messageList = ArrayList()
        memberList = ArrayList()
        val messageAdapter = MessageAdapter(this, messageList)
        val memberAdapter = MemberAdapter(this, memberList)
        binding.recMessages.layoutManager = LinearLayoutManager(this)
        binding.recMessages.adapter = messageAdapter
        binding.recMembers.layoutManager = LinearLayoutManager(this)
        binding.recMembers.adapter = memberAdapter

        curUid = auth.currentUser?.uid!!

        //넘어온 데이터 변수에 담기
        val curChatroom = intent.getStringExtra("chatroom") as String
        val curRoomid = intent.getStringExtra("roomid") as String

        val curRoominfo = curChatroom.split('M')
        val time = if((curRoominfo[2].toInt() / 100) < 10) "${curRoominfo[2][0]}:${curRoominfo[2].substring(1,3)}"
            else "${curRoominfo[2].substring(0, 2)}:${curRoominfo[2].substring(2)}"
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
        
        // 단톡방 인원 확인
        db.child("chats").child(curChatroom).child(curRoomid).child("users")
            .addValueEventListener(object: ValueEventListener {
                //memberList.clear()
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.txtMemcnt.text = "이용자 (" + snapshot.childrenCount + ")"

                    for(postSnapshat in snapshot.children) {
                        val map = postSnapshat.getValue() as HashMap<String, String>    //val uid = postSnapshat.getValue()
                        val uid = map.get("uid") as String
                        //Log.d("CHAT_MEMBERS", uid)

                        storeDb.collection("Nicknames").document(uid).get()
                            .addOnSuccessListener { nicks ->
                                //Log.d("CHAT_MEMBERS_NICK0", nicks.toString())
                                if (nicks.get("nickname") != null)
                                    memberList.add( Member( uid, nicks.get("nickname").toString()))
                                    Log.d("CHAT_MEMBERS_NICK", nicks.get("nickname").toString())
                                    memberAdapter.notifyDataSetChanged()
                                }

                        }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        // 메세지 보내기
        binding.sendBtn.setOnClickListener{
            val message = binding.msgEdit.text.toString()
            val messageObject = Message(message, auth.currentUser?.displayName, markCurTime())  // curName

            // 예약시간의 방으로 메세지 push
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

        // 공지 firebase로 push
        binding.navNotification.setOnClickListener {
            drawer.closeDrawer(GravityCompat.END)
            val dialog = CustomDialog(this)
            dialog.showDialog()
            dialog.setOnClickListener(object: CustomDialog.OnDialogClickListener{
                override fun onClicked(notice: String) {
                    val long_notice = notice

                    // 데이터 setting
                    val notification = Notice(long_notice)

                    // 데이터베이스에 등록
                    db.child("chats").child(curChatroom).child(curRoomid).child("notice")
                        .push().setValue(notification)

                }

            })

        }

        // 공지 변화 업뎃
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