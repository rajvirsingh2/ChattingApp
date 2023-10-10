package com.example.chatting.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.ChatActivity
import com.example.chatting.R
import com.example.chatting.models.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.lang.Exception

class UserAdapter(val context: Context, val userList : ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var auth = FirebaseAuth.getInstance()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.txtName)
//        val imageUrl = itemView.findViewById<ImageView>(R.id.profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.name.text = currentUser.name

        val imageUrl = userList[position].imgUrl
        Picasso.get().load(imageUrl).into(holder.itemView.findViewById(R.id.imageView),object : Callback{
            override fun onSuccess() {
                Toast.makeText(context.applicationContext,"Image Shown", Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: Exception?) {
                Toast.makeText(context.applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
        })

//        Picasso.get().load(imageUrl).into(holder.itemView.findViewById(R.id.action_bar_profile), object : Callback{
//            override fun onSuccess() {
//                TODO("Not yet implemented")
//            }
//
//            override fun onError(e: Exception?) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
//        holder.itemView.findViewById<TextView>(R.id.action_bar_name).text = currentUser.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("img", currentUser.imgUrl)
            intent.putExtra("token",currentUser.FCMToken)
            context.startActivity(intent)
        }

    }

}