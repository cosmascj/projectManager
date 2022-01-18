package com.example.domoreeveryday.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.domoreeveryday.activities.*
import com.example.domoreeveryday.model.Board
import com.example.domoreeveryday.model.User
import com.example.domoreeveryday.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

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


    fun loadUserData(activity: Activity, readBoardList: Boolean = false) {

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
                        activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                    }

                    is MyProfileActivity -> {
                        activity.setUserDataInProfileActivity(loggedInUser)
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

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mfirestore.collection(Constants.BOARD)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.toString(), "Board created successfully")
                Toast.makeText(activity, "Board created sucessfully", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSucessfully()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.toString(), "Error while creating Board", exception)
            }

    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        mfirestore.collection(Constants.BOARD)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                activity.boardDetails(document.toObject(Board::class.java)!!)


            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while creating board", e)
            }
    }

    fun getBoardList(activity: MainActivity) {
        mfirestore.collection(Constants.BOARD)
            .whereArrayContains(Constants.ASSINGED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for (i in document.documents) {
                    var board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.updateBoardListToUI(boardList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while creating board", e)
            }

    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mfirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)

            .addOnSuccessListener {
                Log.i(javaClass.simpleName, "You did it bro!!")
                Toast.makeText(activity, "update success", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while write data", e)
                Toast.makeText(activity, "update unsuccessful", Toast.LENGTH_SHORT).show()


            }

    }
}