package com.example.ballapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballapp.R
import com.example.ballapp.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class ChatDetailsAdapter @Inject constructor(private var list: ArrayList<ChatModel>) :RecyclerView.Adapter<ChatDetailsAdapter.MyViewHolder>(){
    private val MESSAGE_TYPE_RIGHT = 1
    private val MESSAGE_TYPE_LEFT = 0
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    /*
        nếu viewType == MESSAGE_TYPE_RIGHT điển thị một tin nhắn được gửi từ phía người dùng,
        ngược lại nếu viewType không phải là MESSAGE_TYPE_RIGHT, nó sẽ inflate layout item_left_side
        để hiển thị một tin nhắn được gửi từ phía đối tác trò chuyện.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT){
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_right_side,parent,false)
            MyViewHolder(inflater)
        } else {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_right_side,parent,false)
            MyViewHolder(inflater)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(chatList:ArrayList<ChatModel>){
        list = chatList
        notifyDataSetChanged()
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val teamName :TextView = view.findViewById(R.id.chat_team_name)
        val message :TextView = view.findViewById(R.id.chat_message)
        val time : TextView = view.findViewById(R.id.chat_time)
        val image :CircleImageView =view.findViewById(R.id.chat_user_avatar)
    }
    /*

     */
    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
        return if(list[position].senderId == userUID){
            MESSAGE_TYPE_RIGHT
        }else{
            MESSAGE_TYPE_LEFT
        }
    }

    override fun onBindViewHolder(holder: ChatDetailsAdapter.MyViewHolder, position: Int) {
        val chat = list[position]
        holder.message.text = chat.message
        holder.time.text = chat.time
        holder.teamName.text = chat.teamName
        Glide.with(holder.image).load(chat.teamAvatar).centerCrop().into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}