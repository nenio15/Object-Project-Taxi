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
        //val mActivity = activity as MenuActivity

        // nicknames의 roominfo에 대해 받아서 접근시도
        cloudDb.get().addOnSuccessListener { snapshot ->
            if (snapshot == null) return@addOnSuccessListener
            //var list: ArrayList<String>
            if( snapshot.get("roomlist") != null) {
                val list = snapshot.get("roomlist") as ArrayList<String>
                chatroomList.clear()
            for( room in list ) {
                Log.d("MENU_CHATROOM_S", room)
                val roomInfo = snapshot.get(room) as ArrayList<String>
                val roomId = roomInfo[3]

                // uid의 방들을 찾아가라!
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
                            // TODO roominfo도 다시 정렬할것 + 3번째 원소가 추가됨
                            chatroomList.add( ChatRoom( lastMessage, roomInfo[0], roomInfo[1], roomInfo[2], roomInfo[3], cnt))
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