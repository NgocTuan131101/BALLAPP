package com.example.ballapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballapp.databinding.ItemsChatBinding
import com.example.ballapp.`interface`.OnIconClickListerner
import com.example.ballapp.model.ChatModel
import com.example.ballapp.model.UsersModel
import javax.inject.Inject

class ChatAdapter @Inject constructor(private var chatList: ArrayList<UsersModel>) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    private lateinit var listerner: OnIconClickListerner


    fun OnIconClickListerner(listerner: OnIconClickListerner) {
        this.listerner = listerner
    }

    class MyViewHolder(
        private val itemsChatBinding: ItemsChatBinding,
        private val listener: OnIconClickListerner,
    ) : RecyclerView.ViewHolder(itemsChatBinding.root) {
        fun binditems(list: UsersModel) {
            with(itemsChatBinding) {
                teamName.text = list.teamName
                Glide.with(userAvatar).load(list.avatarUrl).centerCrop().into(userAvatar)
                items.setOnClickListener {
                    listener.onIconClick(list)
                }

            }
        }
    }
    @SuppressLint("Noti fyDataSetChanged")
    fun addFilterList(filterList : ArrayList<UsersModel>) {
        chatList = filterList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = ItemsChatBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(items,listerner)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<UsersModel>) {
        chatList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binditems(chatList[position])
    }

}