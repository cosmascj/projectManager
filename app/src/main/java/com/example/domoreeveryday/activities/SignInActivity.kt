package com.example.domoreeveryday.activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.domoreeveryday.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var auth: FirebaseAuth

    private lateinit var et_email: TextView
    private lateinit var et_password: TextView
    private lateinit var btn_signIn: Button

// ...
// Initialize Firebase Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_sign_in)

        toolbar = findViewById(R.id.toolbar_sign_in_activity)
        et_password = findViewById(R.id.sign_in_password)
        et_email = findViewById(R.id.sign_in_email)
        btn_signIn = findViewById(R.id.btn_sign_in)


        setupActionBar()
        auth = FirebaseAuth.getInstance()


        /*
        * Add onclick listener to the sign in button to excute the function called signInUer
        *
        * */

        btn_signIn.setOnClickListener {
            signInUser() }





    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null){

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }


    }

    private fun signInUser(){
        val email = et_email.text.toString().trim {it <= ' '}
        val password = et_password.text.toString().trim{it <= ' '}

        if(validateform(email, password)){
            showProgressDialog(resources.getString(R.string.in_progress_text))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i("registration response", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("registration response", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }


    fun validateform(email : String, password: String): Boolean{
        return when{

            TextUtils.isEmpty(email) -> {
                showSnackBar("Please enter your email")
                false
            }

            TextUtils.isEmpty(password) -> {
                showSnackBar("please enter a password")
                false
            }

            else -> {
                true
            }

        }
    }


}