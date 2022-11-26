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
        val cloudDb = Firebase.firestore.collection(auth.currentUser?.uid!!)
        val mActivity = activity as MenuActivity
        chatroomList = ArrayList()

        val chatlistAdapter = ChatlistAdapter(mActivity, chatroomList)
        binding?.recChat?.layoutManager = LinearLayoutManager(mActivity)
        binding?.recChat?.adapter = chatlistAdapter
        //val mActivity = activity as MenuActivity

        cloudDb.get().addOnSuccessListener { fields ->
            var r: String = ""
            for (field in fields){

                if(field.get("roomlist") != null) r = field.get("roomlist") as String
            }
            Log.d("MENU_CHATROOM", r)

        }

        db.child("chats").child("room").child("roomid")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatroomList.clear()
                    val cnt = snapshot.child("users").childrenCount
                    var lastMessage: Message? = Message()
                    for( message in snapshot.child("messages").children ){
                        lastMessage = message.getValue(Message::class.java)
                    }
                    // TODO 아직 값을 안받았어요..
                    chatroomList.add(ChatRoom(lastMessage, "togo", "time", "by", cnt ))
                    chatlistAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        // 대충, 방을 늘리거나, 현재 방의 메세지가 추가되는 경우



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