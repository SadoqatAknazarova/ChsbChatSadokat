package com.example.chsbchatsadokat.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chsbchatsadokat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editText_name: EditText
    private lateinit var editText_email: EditText
    private lateinit var editText_password: EditText
    private lateinit var editText_confirm_password: EditText
    private lateinit var btn_sign_up: Button
    private lateinit var btn_sign_in: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth= FirebaseAuth.getInstance()
        editText_name = findViewById(R.id.etName)
        editText_email = findViewById(R.id.etEmail)
        editText_password = findViewById(R.id.etPassword)
        editText_confirm_password = findViewById(R.id.etConfirmPassword)
        btn_sign_up=findViewById(R.id.btnSignUp)
        btn_sign_in=findViewById(R.id.btnLogin)
        btn_sign_up.setOnClickListener {
            val userName =editText_name.text
            val email =editText_email.text
            val password =editText_password.text
            val confirm_password =editText_confirm_password.text
            if (TextUtils.isEmpty(userName)){
                Toast.makeText(applicationContext,"username is required", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext,"email is required", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"password is required", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(confirm_password)){
                Toast.makeText(applicationContext,"confirm password is required", Toast.LENGTH_SHORT).show()
            }

            if (!password.equals(confirm_password)){
                Toast.makeText(applicationContext,"password not match", Toast.LENGTH_SHORT).show()
            }
            registerUser(userName.toString(),email.toString(),password.toString())

        }
        btn_sign_in.setOnClickListener {
            var intent = Intent(this@SignUpActivity, LogInActivity::class.java)
            startActivity(intent)
        }
    }
    private fun registerUser(userName:String,email:String,password:String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    val user: FirebaseUser? = auth.currentUser
                    val userId:String = user!!.uid
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap.put("userId",userId)
                    hashMap.put("userName",userName)
                    hashMap.put("profileImage","")
                    databaseReference.setValue(hashMap).addOnCompleteListener(this){
                        if (it.isSuccessful){
                            editText_name.setText("")
                            editText_email.setText("")
                            editText_password.setText("")
                            editText_confirm_password.setText("")
                            val intent = Intent(this@SignUpActivity, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
    }
}