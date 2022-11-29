package com.example.taxicar_app

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_chatroom.view.*

//왜 data더라..
data class ChatRoom( val message: Message?, val togo: String?, val by: String?, val time: String?, val roomid: String?, val cnt: Long){
    constructor(): this(Message(), "", "", "", "roomM0",0)
}

class ChatlistAdapter(private val mActivity: MenuActivity, private val chatroomList: ArrayList<ChatRoom>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //val binding = ListChatroomBinding.inflate(LayoutInflater.from(parent.context))
        //return ViewHolder()
        val view: View = LayoutInflater.from(mActivity).inflate(R.layout.list_chatroom, parent, false)
        return ViewHolder(view, mActivity)

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentChatRoom = chatroomList[position]

        val viewHolder = holder as ViewHolder

        viewHolder.getRoom(currentChatRoom, position)
        viewHolder.bind(currentChatRoom)

    }

    class ViewHolder(itemView: View, private val mActivity: MenuActivity)
        : RecyclerView.ViewHolder(itemView){
            fun bind(chatroom: ChatRoom) {
                itemView.setOnClickListener {
                    // 갱신 후 intent
                    mActivity.whereTogo = Timedata(chatroom.by!!, chatroom.togo!!)
                    mActivity.goIntent(chatroom.time!!) // 그냥 될걸?
                    Log.d("CHAT_MENU_GET", "단톡방을 건들였습니다. ${itemView.txt_message}")
                }
            }

            // 갱신
            fun getRoom(chatroom: ChatRoom, recent: Int){
                var togo: String = "기숙사"
                if( chatroom.togo.equals("toSchool")) togo = "학교"
                // 문자 메세지가 너무 길면 잘라야 하지 않나요..?
                itemView.txt_message.text = "${chatroom.message?.sendName} : ${chatroom.message?.message}"
                itemView.txt_togo.text = togo

                //var time: String = chatroom.time!!
                //if( chatroom.time.length == 4) time = "${chatroom.time[:1]}"
                itemView.txt_reservetime.text = chatroom.time
                itemView.txt_users.text = chatroom.cnt.toString()
                if(chatroom.by.equals("Taxi")) itemView.img_taxi.visibility = View.VISIBLE
                if(chatroom.by.equals("Carpool")) itemView.img_carpool.visibility = View.VISIBLE
                if(recent == 0) itemView.setBackgroundColor(Color.parseColor("#E6FFE08C"))

            }

        }

    override fun getItemCount() = chatroomList.size

    // need this?
    override fun getItemViewType(position: Int): Int {
        return 1

    }
}