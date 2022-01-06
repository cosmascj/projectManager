package com.example.domoreeveryday.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.domoreeveryday.R
import com.example.domoreeveryday.firebase.firestore
import com.example.domoreeveryday.model.SplashViewModel

class IntroActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var btn_signUp: Button
    private lateinit var btn_signIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DoMoreEveryDay)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        installSplashScreen().apply {
            setKeepVisibleCondition {
                viewModel.isLoading.value
            }
        }

        setContentView(R.layout.activity_intro)
        btn_signUp = findViewById(R.id.btn_sign_up_intro)
        btn_signIn = findViewById(R.id.btn_sign_in_intro)

        var currentUserID = firestore().getCurrentUserID()

        if (currentUserID.isNotEmpty()){
            startActivity(Intent(this, MainActivity ::class.java))
        }else{
            startActivity(Intent(this, SignUpActivity ::class.java))

        }


        btn_signUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity ::class.java))
        }

        btn_signIn.setOnClickListener{
            startActivity(Intent(this, SignInActivity ::class.java))
        }

    }
}