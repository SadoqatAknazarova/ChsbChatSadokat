package com.example.chsbchatsadokat.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chsbchatsadokat.R
import com.example.chsbchatsadokat.adapter.UserAdapter
import com.example.chsbchatsadokat.firebasechat.firebase.FirebaseService
import com.example.chsbchatsadokat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class UsersActivity : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var btnBack_img: ImageView
    private lateinit var imgProfile: ImageView
    var userList=ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        btnBack_img=findViewById(R.id.imgBack)
        imgProfile=findViewById(R.id.imgProfile)
        FirebaseService.sharedPref=getSharedPreferences("sharedPref", MODE_PRIVATE)
        /* FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
             FirebaseService.token = it.token
         }*/
        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if(result != null){
                FirebaseService.token = result
            }
        }
        btnBack_img.setOnClickListener {
            onBackPressed()
        }
        imgProfile.setOnClickListener {
            val intent = Intent(
                this@UsersActivity,
                ProfileActivity::class.java
            )
            startActivity(intent)
        }
        getUsersList()
    }
    fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == ""){
                    imgProfile.setImageResource(R.drawable.profile_image)
                }else{
                    Glide.with(this@UsersActivity).load(currentUser.profileImage).into(imgProfile)
                }
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    if (!user!!.userId.equals(firebase.uid)) {
                        userList.add(user)
                    }
                }
                val userAdapter = UserAdapter(this@UsersActivity, userList)
                userRecyclerView.adapter = userAdapter
            }
        })
    }
}