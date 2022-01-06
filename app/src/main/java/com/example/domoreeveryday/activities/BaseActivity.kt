package com.example.domoreeveryday.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.domoreeveryday.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


/*
* This class is open because the functions here will be reused in other activities
* */
open class BaseActivity : AppCompatActivity() {
private lateinit var progress_text : TextView
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog

    fun showProgressDialog(text: String){

       // progress_text = findViewById(R.id.tv_progress_text)

        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)

       // mprogressDialog.tv_progress_text.text =text
     //  mProgressDialog.context.getString() = text
        mProgressDialog.show()


    }

    fun hideProgressDialog(){

        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true

Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({doubleBackToExitPressedOnce =false}, 2000)
    }

/*
* We will be getting just the user id for now to help
* display the tasks each user has been been assigned to
* */
    fun getCurrentUserID(): String{

    return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun showSnackBar(message: String){

        var snackbar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)

        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(
            this, R.color.snackbar_error_color))
            snackbar.show()
    }



}