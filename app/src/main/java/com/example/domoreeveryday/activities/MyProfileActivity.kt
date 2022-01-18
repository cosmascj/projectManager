package com.example.domoreeveryday.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
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
import com.example.domoreeveryday.model.User
import com.example.domoreeveryday.utils.Constants
import com.example.domoreeveryday.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.example.domoreeveryday.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private lateinit var toolbar_my_profile_activity: Toolbar
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var updateImage: ImageView
    private lateinit var updateImage_btn: Button
    private lateinit var mUserDetails: User

    private var profileImageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        toolbar_my_profile_activity = findViewById(R.id.toolbar_my_profile_activity)
        updateImage = findViewById(R.id.update_user_profile_image)
        updateImage_btn = findViewById(R.id.btn_update)

        setupActionBar()

        FireStoreClass().loadUserData(this)
        updateImage_btn.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                upLoadImage()
            } else {
                showProgressDialog(resources.getString(R.string.in_progress_text))
                updateUserProfileData()
            }
        }


        updateImage.setOnClickListener {

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
                    READ_STORAGE_PERMISSION_CODE
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this@MyProfileActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(updateImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted


            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)


            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back_arrow_24)
            actionBar.title = resources.getString(R.string.my_profile)

        }

        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun setUserDataInProfileActivity(user: User) {

        mUserDetails = user
        val userImage = findViewById<ImageView>(R.id.update_user_profile_image)
        val userProfileName = findViewById<TextView>(R.id.update_profile_name)
        val userProfileMobileNumber = findViewById<TextView>(R.id.update_mobile_number)
        val userEmailAddress = findViewById<TextView>(R.id.user_profile_email)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userImage)

        userProfileName.setText(user.name)
        userEmailAddress.setText(user.email)
        if (user.mobile != 0L) {
            userProfileMobileNumber.setText(user.mobile.toString())
        }
    }

    fun updateUserProfileData() {
        val userProfileName = findViewById<TextView>(R.id.update_profile_name)
        val userProfileMobileNumber = findViewById<TextView>(R.id.update_mobile_number)

        var userHashMap = HashMap<String, Any>()

        if (profileImageUri.isNotEmpty() && profileImageUri != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = profileImageUri
        }
        if (userProfileName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = userProfileName.text.toString()
        }

        if (userProfileMobileNumber.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = userProfileMobileNumber.text.toString().toLong()

        }
        FireStoreClass().updateUserProfileData(this, userHashMap)
        // Toast.makeText(this,"wwwwwwoooo",Toast.LENGTH_SHORT).show()
    }


    private fun upLoadImage() {
        showProgressDialog(resources.getString(R.string.in_progress_text))

        if (mSelectedImageFileUri != null) {

            val storageRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child(
                    "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                        this,
                        mSelectedImageFileUri
                    )
                )

            storageRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i(
                        "image upload reference",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.i("Downloadable Uri", uri.toString())
                        profileImageUri = uri.toString()


                        updateUserProfileData()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
        }

    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()


    }
}