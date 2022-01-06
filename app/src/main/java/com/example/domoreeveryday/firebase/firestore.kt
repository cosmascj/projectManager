package com.example.domoreeveryday.firebase

import android.app.Activity
import android.util.Log
import com.example.domoreeveryday.activities.MainActivity
import com.example.domoreeveryday.activities.SignInActivity
import com.example.domoreeveryday.activities.SignUpActivity
import com.example.domoreeveryday.model.User
import com.example.domoreeveryday.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class firestore {

    private val mfirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mfirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Errrrror", e)
            }
    }


    fun signInUser(activity: Activity) {

        mfirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            //get the details from the database and make an object of it using "toObject"
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!


                when (activity) {
                    is SignInActivity -> {
                        /*if (loggedInUser != null) {
                            activity.signInSuccess(loggedInUser)
                        }*/
                        activity.signInSuccess(loggedInUser)

                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                }


            }
            .addOnFailureListener { e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Errrrror", e)
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}