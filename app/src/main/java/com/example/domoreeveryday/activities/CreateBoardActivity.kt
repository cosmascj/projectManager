package com.example.domoreeveryday.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.domoreeveryday.R
import com.example.domoreeveryday.firebase.FireStoreClass
import com.example.domoreeveryday.model.Board
import com.example.domoreeveryday.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var board_image: ImageView
    private lateinit var mUserName: String
    private lateinit var boardName: TextView
    private lateinit var createBtn: Button
    private var mBoardImageUri: String = ""

    private lateinit var toolbar_create_board_activity: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        board_image = findViewById(R.id.iv_board_image)
        toolbar_create_board_activity = findViewById(R.id.toolbar_create_board_activity)
        boardName= findViewById(R.id.et_board_name)
        createBtn= findViewById(R.id.btn_create)

        if(intent.hasExtra(Constants.NAME)){
            // please initialize musername
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        setupActionBar()


        board_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {

                Constants.showImageChooser(this)

            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        createBtn.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.in_progress_text))
                    createBoard()
            }
        }


    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_board_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        toolbar_create_board_activity.setNavigationOnClickListener { onBackPressed() }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(board_image) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }




    fun uploadBoardImage(){
showProgressDialog(resources.getString(R.string.in_progress_text))

        if (mSelectedImageFileUri != null) {

            val storageRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child(
                    "BOARD_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                        this,
                        mSelectedImageFileUri
                    )
                )

            storageRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i(
                        "Firebase Boardimage uri",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.i("Downloadable Uri", uri.toString())
                        mBoardImageUri = uri.toString()


                        createBoard()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
        }



    }


    fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())

        var board = Board(
            boardName.text.toString(),

            mBoardImageUri,
            mUserName,
            assignedUserArrayList)

        FireStoreClass().createBoard(this, board)

    }

    fun boardCreatedSucessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }





}