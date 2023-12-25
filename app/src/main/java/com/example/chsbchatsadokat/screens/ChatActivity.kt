package com.example.chsbchatsadokat.screens

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chsbchatsadokat.R
import com.example.chsbchatsadokat.adapter.ChatAdapter
import com.example.chsbchatsadokat.firebasechat.RetrofitInstance
import com.example.chsbchatsadokat.firebasechat.model.NotificationData
import com.example.chsbchatsadokat.firebasechat.model.PushNotification

import com.example.chsbchatsadokat.model.Chat
import com.example.chsbchatsadokat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var message_text: EditText
    private lateinit var imgBack: ImageView
    private lateinit var btn_send: ImageButton
    private lateinit var chatRecyclerView: RecyclerView
    var firebaseUser: FirebaseUser?=null
    var reference: DatabaseReference?=null
    var topic = ""
    var chatList=ArrayList<Chat>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatRecyclerView=findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager= LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        var intent=getIntent()
        var userId=intent.getStringExtra("userId")
        var userName = intent.getStringExtra("userName")
        firebaseUser= FirebaseAuth.getInstance().currentUser
        imgProfile=findViewById(R.id.imgProfile)
        tvUserName=findViewById(R.id.tvUserName)
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        imgBack=findViewById(R.id.imgBack)
        btn_send=findViewById(R.id.btnSendMessage)
        message_text=findViewById(R.id.etMessage)
        reference!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                tvUserName.text = user!!.userName
                if (user.profileImage == "") {
                    imgProfile.setImageResource(R.drawable.profile_image)
                } else {
                    Glide.with(this@ChatActivity).load(user.profileImage).into(imgProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        btn_send.setOnClickListener {
            var message: String = message_text.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                message_text.setText("")
            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
                message_text.setText("")
                topic = "/topics/$userId"
                PushNotification(
                    NotificationData( userName!!,message),
                    topic).also {
                    sendNotification(it)
                }
            }
        }
        imgBack.setOnClickListener {
            onBackPressed()
        }
        btn_send.setOnClickListener {
            var message:String=message_text.text.toString()
            if(message.isEmpty()){
                Toast.makeText(applicationContext,"message is empty", Toast.LENGTH_SHORT).show()
            }else {
                sendMessage(firebaseUser!!.uid, userId, message)
                message_text.setText("")
                topic = "/topics/$userId"
                PushNotification(
                    NotificationData( userName!!,message),
                    topic).also {
                    sendNotification(it)
                }
            }
        }
        readMessage(firebaseUser!!.uid, userId)
    }

    private fun sendMessage(senderId:String,receiverId:String,message:String){
        var reference: DatabaseReference?= FirebaseDatabase.getInstance().getReference()
        var hashMap:HashMap<String,String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)
        reference!!.child("Chat").push().setValue(hashMap)
    }

    fun readMessage(senderId: String, receiverId: String){
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }
                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)
                chatRecyclerView.adapter = chatAdapter
            }
        })
    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }
}