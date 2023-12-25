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

class LogInActivity : AppCompatActivity() {
    private lateinit var editText_email: EditText
    private lateinit var editText_password: EditText
    private lateinit var btn_login: Button
    private lateinit var btn_to_sign_up: Button
    private var auth: FirebaseAuth?=null
    private var firebaseUser: FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth= FirebaseAuth.getInstance()
        //   firebaseUser=auth.currentUser!!
        if(firebaseUser!=null){
            val intent = Intent(this@LogInActivity, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
        editText_email = findViewById(R.id.etEmail)
        editText_password = findViewById(R.id.etPassword)
        btn_login=findViewById(R.id.btnLogin)
        btn_to_sign_up=findViewById(R.id.btnSignUp)
        btn_login.setOnClickListener {
            val email =editText_email.text.toString()
            val password =editText_password.text.toString()
            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "email and password are required", Toast.LENGTH_SHORT).show()
            } else{
                auth!!.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){
                        if(it.isSuccessful){
                            editText_email.setText("")
                            editText_password.setText("")
                            val intent = Intent(this@LogInActivity, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "email or password invalid", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        btn_to_sign_up.setOnClickListener {
            val intent = Intent(
                this@LogInActivity,
                SignUpActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}