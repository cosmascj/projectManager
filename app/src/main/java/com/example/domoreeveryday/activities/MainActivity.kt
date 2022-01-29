package com.example.domoreeveryday.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.domoreeveryday.R
import com.example.domoreeveryday.adapters.BoardItemsAdapter
import com.example.domoreeveryday.databinding.ActivityMainBinding
import com.example.domoreeveryday.firebase.FireStoreClass
import com.example.domoreeveryday.model.Board
import com.example.domoreeveryday.model.User
import com.example.domoreeveryday.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var fab_button: FloatingActionButton
    private lateinit var mUserName: String
    private lateinit var recyclerViewboardsList: RecyclerView
    private lateinit var noBoardsAvilableText: TextView

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    lateinit var userName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setTheme(R.style.Theme_DoMoreEveryDay)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )



        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.tool_bar_mainActivity)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        fab_button = findViewById(R.id.fab_create_board)
        navigationView.setNavigationItemSelectedListener(this)
        recyclerViewboardsList = findViewById(R.id.rv_boards_list)
        noBoardsAvilableText = findViewById(R.id.tv_no_boards_available)

        setupActionBar()
        FireStoreClass().loadUserData(this, true)

        fab_button.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }

    fun updateBoardListToUI(BoardsList: ArrayList<Board>) {

        hideProgressDialog()
            if (BoardsList.size > 0){
                recyclerViewboardsList.visibility = View.VISIBLE
                noBoardsAvilableText.visibility = View.GONE
                recyclerViewboardsList.layoutManager = LinearLayoutManager(this)
                recyclerViewboardsList.setHasFixedSize(true)

                val adapter = BoardItemsAdapter(this, BoardsList)
                recyclerViewboardsList.adapter = adapter

                adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{

                    override fun onClick(position: Int, model: Board) {
                   var intent = Intent(this@MainActivity, TaskListActivity::class.java)
                        intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                    }
                })

            }else{
                recyclerViewboardsList.visibility = View.GONE
                noBoardsAvilableText.visibility = View.VISIBLE
            }

    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer_opener_24)

        toolbar.setNavigationOnClickListener { toogleDrawer() }

    }

    private fun toogleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FireStoreClass().loadUserData(this)
        } else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FireStoreClass().getBoardList(this)

        }

        else {
            Log.i("Update cancelled", "cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {

                //To start the activity with a specific request, to update the navigation header with the new details of the user
                startActivityForResult(
                    Intent(
                        this@MainActivity,
                        MyProfileActivity::class.java
                    ),
                    MY_PROFILE_REQUEST_CODE
                )
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        mUserName = user.name.toString()

        // The instance of the header view of the navigation view.
        val headerView = navigationView.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)
        //  userImage = findViewById(R.id.iv_user_image)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage);


       val navUserName= headerView.findViewById<TextView>(R.id.tv_username)

        navUserName.text = user.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.in_progress_text))
            FireStoreClass().getBoardList(this)
        }
    }

}