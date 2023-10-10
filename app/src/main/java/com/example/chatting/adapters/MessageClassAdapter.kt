package com.example.chatting.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatting.R
import com.example.chatting.models.Message
import com.google.firebase.auth.FirebaseAuth

class MessageClassAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var auth = FirebaseAuth.getInstance()

    val item_recived = 1
    val item_sent = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if(viewType == 1){
            //inflate receiver one!!
            val view: View = LayoutInflater.from(context).inflate(R.layout.recieve_message, parent,false)
            return receiveViewHolder(view)

        }else{
            // inflate sender one!!
            val view:  View = LayoutInflater.from(context).inflate(R.layout.sent_layout, parent, false)
            return sentViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentMessage = messageList[position]
        // For sent view holder!!!
        if(holder.javaClass == sentViewHolder::class.java){
            val viewHolder = holder as sentViewHolder
            holder.sentMessage.text = currentMessage.message
        }

        // For receive view holder!!!
        else{
            val viewHolder = holder as receiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(auth.currentUser?.uid.equals(currentMessage.senderId)){
            //Equals is used bcz string is there{
            return item_sent
        }else{
            return item_recived
        }
    }


    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.sent_message)
    }

    class receiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.recieved_message)
    }

}