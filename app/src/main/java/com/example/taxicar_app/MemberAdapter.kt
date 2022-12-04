package com.example.taxicar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_member.view.*

data class Uid( val uid: String?) {constructor(): this("")}
data class Member( val uid: String, val nickname: String?)

class MemberAdapter(private val activity: ChatActivity, private val memberlist: ArrayList<Member>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.list_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMember = memberlist[position]
        val viewHolder = holder as ViewHolder

        viewHolder.bind(currentMember)
    }

    class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        fun bind(member: Member){
            itemView.txt_cur_time.text = member.nickname
            // 누르면, 위치를 보여주기엔 시간 부족
        }

    }

    override fun getItemCount() = memberlist.size


}