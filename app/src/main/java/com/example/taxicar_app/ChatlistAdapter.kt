package com.example.taxicar_app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.taxicar_app.databinding.ListChatroomBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.list_receive.view.*
import kotlinx.android.synthetic.main.list_send.view.*

data class ChatRoom( val message: Message?, val togo: String?, val time: String?, val by: String?, val cnt: Long){
    constructor(): this(Message(), "", "", "", 0)
}

class ChatlistAdapter(private val mActivity: MenuActivity, private val chatroomList: ArrayList<ChatRoom>)
    : RecyclerView.Adapter<ChatlistAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListChatroomBinding.inflate(LayoutInflater.from(parent.context))
        //return ViewHolder()
        return Holder(binding, mActivity)

    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val currentChatRoom = chatroomList[position]

        holder.bind()
        holder.getRoom(currentChatRoom, position)

    }

    class Holder(private val binding: ListChatroomBinding, private val mActivity: MenuActivity)
        : RecyclerView.ViewHolder(binding.root){
            fun bind() {
                binding.root.setOnClickListener {
                    Toast.makeText(
                        mActivity,
                        "단톡방을 건들였다!!! ${binding.txtMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // 갱신
            fun getRoom(chatroom: ChatRoom, recent: Int){
                binding.txtMessage.text = "${chatroom.message?.sendName} : ${chatroom.message?.message}"
                binding.txtTogo.text = chatroom.togo
                binding.txtReservetime.text = chatroom.time
                binding.txtUsers.text = chatroom.time.toString()

                if(recent == 1) binding.root.setBackgroundColor(Color.parseColor("#FFFF66"))

            }

        }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return 1


    }
    /*
    // 보낸 쪽
    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //val sendMessage: TextView = itemView.findViewById(R.id.send_msg)
        val sendMsg = itemView.send_msg
        val txtTime = itemView.txt_cur_time
    }
    // 받는 쪽
    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //val receiveMessage: TextView = itemView.findViewById(R.id.receive_msg)
        val receiveMsg = itemView.receive_msg
        val txtTime = itemView.txt_that_time
        val txtNickname = itemView.txt_nickname
    }

     */
}