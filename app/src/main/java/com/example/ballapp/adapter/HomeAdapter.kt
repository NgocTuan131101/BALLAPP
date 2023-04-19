package com.example.ballapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballapp.databinding.ItemsBinding
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballball.model.CreateMatchModel
import javax.inject.Inject

class HomeAdapter @Inject constructor(private var requestList: ArrayList<CreateMatchModel>) :
    RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {
    private lateinit var listerner: OnItemClickListerner

    fun setOnItemClickListerner(listerner: OnItemClickListerner) {
        this.listerner = listerner
    }

    class MyViewHolder(
        private val bindingitems: ItemsBinding,
        private val listener: OnItemClickListerner,
    ) : RecyclerView.ViewHolder(bindingitems.root) {
        fun binditems(list: CreateMatchModel) {
            with(bindingitems) {
                teamName.text = list.teamName
                date.text = list.date
                time.text = list.time
                location.text = list.location
                peopleNumber.text = list.teamPeopleNumber
                address.text = list.locationAddress
                Glide.with(teamImage).load(list.teamImageUrl).centerCrop().into(teamImage)
                newCreate.visibility = View.GONE
                waitConfirm.visibility = View.GONE
                highlightIcon1.visibility = View.GONE
                highlightIcon2.visibility = View.GONE
                if (list.highlight == 1) {
                    highlightIcon1.visibility = View.VISIBLE
                }
                if (list.highlight == 0) {
                    highlightIcon2.visibility = View.GONE
                }
                items.setOnClickListener {
                    listener.onItemClick(list)
                }

                highlightIcon1.setOnClickListener {
                    highlightIcon1.visibility = View.GONE
                    highlightIcon2.visibility = View.VISIBLE
                }
                highlightIcon2.setOnClickListener {
                    highlightIcon1.visibility = View.GONE
                    highlightIcon2.visibility = View.VISIBLE
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
       return MyViewHolder(items,listerner)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binditems(requestList[position])
    }
    fun addNewData(list: ArrayList<CreateMatchModel>) {
        requestList = list
        notifyDataSetChanged()
    }

    @SuppressLint("Noti fyDataSetChanged")
    fun addFilterList(filterList : ArrayList<CreateMatchModel>) {
        requestList = filterList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return requestList.size
    }
}