package com.example.domoreeveryday.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.domoreeveryday.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


/*
* This activity will inherite the functions in BaseActivity. This is to enable me reuse are the functions
* created in the BaseActivity
* */
class SignUpActivity : BaseActivity() {
    private lateinit var et_name: TextView
    private lateinit var et_email: TextView
    private lateinit var et_password: TextView
    private lateinit var btn_signUp: Button

    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setTheme(R.style.Theme_DoMoreEveryDay)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_sign_up)

        toolbar = findViewById(R.id.toolbar_sign_up_activity)

        setupActionBar()
        et_name = findViewById(R.id.et_name)
        et_password = findViewById(R.id.et_password)
        et_email = findViewById(R.id.et_email)
        btn_signUp = findViewById(R.id.btn_sign_up)


        btn_signUp.setOnClickListener {
            registerUser() }
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

    private fun registerUser(){
        val name: String = et_name.text.toString().trim(){it <= ' '}
        val email: String = et_email.text.toString().trim{it <= ' '}
        val password: String = et_password.text.toString().trim{it <= ' '}


        if (validateform(name, email, password)){
            showProgressDialog(resources.getString(R.string.in_progress_text))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    task -> hideProgressDialog()
                    if (task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        var registeredEmail = firebaseUser.email!!
                        Toast.makeText(this, "$name your Registration with $registeredEmail  is successful", Toast.LENGTH_SHORT)
                            .show()
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    }else {
                        Toast.makeText(this, task.exception!!.message,Toast.LENGTH_LONG)
                            .show()
                    }
                }



        }
    }

    fun validateform(name: String, email : String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(name) -> {
                showSnackBar("Please enter your name")
                false
            }


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