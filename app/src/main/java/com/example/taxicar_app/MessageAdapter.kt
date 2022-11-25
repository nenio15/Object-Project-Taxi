package com.example.taxicar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.list_receive.view.*
import kotlinx.android.synthetic.main.list_send.view.*
import kotlinx.android.synthetic.main.list_timeline.view.*
import java.sql.Timestamp

data class Message( val message: String?, var sendName: String?, val time: String?){
    constructor(): this("","", "")
}

class MessageAdapter(private val activity: ChatActivity, private val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val receive = 1 // 받는 타입
    private val send = 2 // 보내는 타입

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1){ // 받는 화면
            //TODO need to merge too.. and R...?
            val view: View = LayoutInflater.from(activity).inflate(R.layout.list_receive, parent, false)
            ReceiveViewHolder(view)
        }else{ // 보내는 화면
            val view: View = LayoutInflater.from(activity).inflate(R.layout.list_send, parent, false)
            SendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 현재 메시지
        val currentMessage = messageList[position]

        // 보내는 데이터
        if(holder.javaClass == SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMsg.text = currentMessage.message
            viewHolder.txtTime.text = currentMessage.time

        }else{ // 받는 데이터
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMsg.text = currentMessage.message
            viewHolder.txtTime.text = currentMessage.time
            viewHolder.txtNickname.text = currentMessage.sendName
        }
    }


    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {

        // 메시지값
        val currentMessage = messageList[position]

        return if(FirebaseAuth.getInstance().currentUser?.displayName.equals(currentMessage.sendName)){ //user의 name이랑 equal비교가 필요함..
            send
        }else{
            receive
        }
    }

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

}