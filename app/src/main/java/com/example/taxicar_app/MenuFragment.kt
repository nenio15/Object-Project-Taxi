package com.example.taxicar_app

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxicar_app.databinding.FragmentMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    var binding: FragmentMenuBinding? = null
    lateinit var auth: FirebaseAuth
    lateinit var db: DatabaseReference
    private lateinit var chatroomList: ArrayList<ChatRoom>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance().reference
        val cloudDb = Firebase.firestore.collection("Nicknames").document(auth.currentUser?.uid!!)
        val mActivity = activity as MenuActivity
        chatroomList = ArrayList()

        val chatlistAdapter = ChatlistAdapter(mActivity, chatroomList)
        binding?.recChat?.layoutManager = LinearLayoutManager(mActivity)
        binding?.recChat?.adapter = chatlistAdapter
        //var chatroomLength: Int = 0

        // nicknames의 roominfo에 대해 받아서 접근시도
        cloudDb.get().addOnSuccessListener { snapshot ->
            if (snapshot == null) return@addOnSuccessListener
            //var list: ArrayList<String>
            if( snapshot.get("roomlist") != null) {
                val list = snapshot.get("roomlist") as ArrayList<String>
                chatroomList.clear()
                list.forEachIndexed{ index, room ->
                    Log.d("MENU_CHATROOM_S", room)
                    val roomInfo = snapshot.get(room) as ArrayList<String>
                    val roomId = roomInfo[3]

                    // uid의 방들을 찾아가라!
                    // 순서 바뀔때 색깔 문제가 약간 있는듯.. (clear, add의 문제. add라서 후열로 인식됨..인가? 애초에 메세지만이 아니라 그거는..)
                    db.child("chats").child(room).child(roomId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {  // 업데이트 입니다. 머 굳이 업데이트일 필요가 있겠냐면은, 최근 메세지를 보기위해서?

                                // 단톡방 사람수, 그리고 메세지
                                val cnt = snapshot.child("users").childrenCount
                                var lastMessage: Message? = Message()
                                for (message in snapshot.child("messages").children) {
                                    // 사실 마지막 메세지만 받고 싶어요..
                                    lastMessage = message.getValue(Message::class.java)
                                }
                                // set으로 변경이 제일 무난, 근데 맨 처음엔 add여야하지 않나요? 그럼그렇지 (이것도 오류가 있네요.. 단순하게 정리하려해도 시간이 순삭이 되네요. 무리)
                                chatroomList.add(ChatRoom( lastMessage, roomInfo[0], roomInfo[1], roomInfo[2], roomInfo[3], cnt))
                                Log.d("MENU_ADDED", "i added chatroomlist ${lastMessage?.message}")
                                chatlistAdapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }
            }

            //val n = fields.get(r) as String


        }

        return binding?.root//inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            MenuFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}