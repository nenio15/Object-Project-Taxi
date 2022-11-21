package com.example.taxicar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityChatBinding
    private var mFirebaseD: FirebaseDatabase = FirebaseDatabase.getInstance()
    //아래 문장이 realtime base를 get하는것. 원리는 json의 key값을 받는 형식과 같다.
    private var mDatabaseR: DatabaseReference = mFirebaseD.getReference()

    private lateinit var receiveName: String
    private lateinit var receiveUid: String
    lateinit var db: DatabaseReference
    private lateinit var receiverRoom: String
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
        receiveName = intent.getStringExtra("name").toString()
        receiveUid = intent.getStringExtra("uid").toString()

        // setting
        val senderUid = auth.currentUser?.uid
        // maybe.. need to conver to merge in one room
        senderRoom = receiveUid + senderUid
        receiverRoom = senderUid + receiveUid

        supportActionBar?.title = receiveName   // this is working?

        binding.sendBtn.setOnClickListener{
            val message = binding.msgEdit.text.toString()
            val messageObject = Message(message, senderUid)

            db.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //TODO this need to merge one rooms..
                    db.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }
            binding.msgEdit.setText("")
        }

        binding.btnTest3.setOnClickListener{
            finish()
        }

        db.child("chats").child(senderRoom).child("messages")
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