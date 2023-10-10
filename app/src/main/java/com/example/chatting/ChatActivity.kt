package com.example.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.adapters.MessageClassAdapter
import com.example.chatting.databinding.ActivityChatBinding
import com.example.chatting.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    lateinit var chatBinding: ActivityChatBinding
    lateinit var chatRecyclerView: RecyclerView
    lateinit var messgaBox: EditText
    lateinit var profileImage: ImageView
    lateinit var profileName: TextView
    lateinit var sendButton: ImageView
    lateinit var messageAdapter: MessageClassAdapter
    lateinit var messageList: ArrayList<Message>
    var receiverRoom: String? = null  // Creates a separate room for receiver
    var senderRoom: String? = null
    var auth = FirebaseAuth.getInstance()
    var dbRef:DatabaseReference = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        val view = chatBinding.root
        setContentView(view)

        chatRecyclerView = chatBinding.chatRecycler
        messgaBox = chatBinding.chatbox
        sendButton = chatBinding.sendButton
        messageList = ArrayList()
        messageAdapter = MessageClassAdapter(this,messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        val name = intent.getStringExtra("name")
        val receiveruid  = intent.getStringExtra("uid")
        val senderUid = auth.currentUser?.uid

        senderRoom = receiveruid + senderUid
        receiverRoom = senderUid + receiveruid



        // For adding to the recycler view ->

        dbRef.child("chats").child(senderRoom!!).child("message").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                messageList.clear()

                for(postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        // Add message to database
        sendButton.setOnClickListener {
            val message = chatBinding.chatbox.text.toString()
            val messageObject = Message(message,senderUid)

            dbRef.child("chats").child(senderRoom!!).child("message").push()
                .setValue(messageObject).addOnSuccessListener {
                    dbRef.child("chats").child(receiverRoom!!).child("message").push()
                        .setValue(messageObject)
                }
            chatBinding.chatbox.setText("")
        }
    }
}

