package com.example.domoreeveryday.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domoreeveryday.R
import com.example.domoreeveryday.adapters.MembersListItemsAdapter
import com.example.domoreeveryday.firebase.FireStoreClass
import com.example.domoreeveryday.model.Board
import com.example.domoreeveryday.model.User
import com.example.domoreeveryday.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MembersActivity : BaseActivity() {
    private lateinit var toolbar_members_activity: Toolbar
    private lateinit var rv_member_list: RecyclerView
    private lateinit var mAssignedMembersList: ArrayList<User>
    private lateinit var fab_button: FloatingActionButton
    private var anyChangesmade: Boolean = false



    private lateinit var mBoardDetails: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        toolbar_members_activity = findViewById(R.id.toolbar_members_activity)
        rv_member_list = findViewById(R.id.rv_members_list)
        fab_button = findViewById(R.id.fab_create_member)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        showProgressDialog(resources.getString(R.string.in_progress_text))
        FireStoreClass().getAssignedMemberListDetails(this, mBoardDetails.assignedTo)

        setupActionBar()

        fab_button.setOnClickListener {
            dialogSearchMember()
        }


    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back_arrow_24)
            actionBar.title = resources.getString(R.string.my_profile)

        }

        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUpMembersListInUI(list: ArrayList<User>) {
        mAssignedMembersList = list
        hideProgressDialog()
        rv_member_list.layoutManager = LinearLayoutManager(this)
        rv_member_list.setHasFixedSize(true)

        var adapter = MembersListItemsAdapter(this, list)
        rv_member_list.adapter = adapter


    }

    fun memberDetails(user: User){
        user.id?.let { mBoardDetails.assignedTo.add(it) }
        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
               // dialogSearchMember()
                Toast.makeText(this, "Test me", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }


    fun dialogSearchMember() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener(View.OnClickListener {

            val email = dialog.findViewById<TextView>(R.id.et_email_search_member).text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.in_progress_text))
                FireStoreClass().getMemberDetails(this@MembersActivity, email)
            } else {
               showSnackBar("Please enter an email address")

            }
        })

        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {

        if (anyChangesmade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
fun memberAssignedSuccess(user: User)
{
    hideProgressDialog()
    mAssignedMembersList.add(user)
    anyChangesmade = true
    setUpMembersListInUI(mAssignedMembersList)


}


}