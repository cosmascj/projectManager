package com.example.domoreeveryday.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domoreeveryday.R
import com.example.domoreeveryday.adapters.TaskListItemAdapter
import com.example.domoreeveryday.firebase.FireStoreClass
import com.example.domoreeveryday.model.Board
import com.example.domoreeveryday.model.Card
import com.example.domoreeveryday.model.Task
import com.example.domoreeveryday.model.User
import com.example.domoreeveryday.utils.Constants

open class TaskListActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var rv_task_list: RecyclerView
    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    private lateinit var mAssignedMemberDetailsList: ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        toolbar = findViewById(R.id.toolbar_task_list_activity)
        rv_task_list = findViewById(R.id.rv_task_list)


        // var boardDocumentId : String= ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            showProgressDialog(resources.getString(R.string.in_progress_text))
            FireStoreClass().getBoardDetails(this, mBoardDocumentId)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        var intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailsList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_OK && requestCode == MEMBERs_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE) {
            showProgressDialog(resources.getString(R.string.in_progress_text))
            FireStoreClass().getBoardDetails(this, mBoardDocumentId)
        }
    }

    fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        rv_task_list.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        rv_task_list.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(this, board.taskList)
        rv_task_list.adapter = adapter

        showProgressDialog(resources.getString(R.string.in_progress_text))
        FireStoreClass().getAssignedMemberListDetails(this, mBoardDetails.assignedTo)

    }

    fun createTaskList(taskListName: String) {

        val task = Task(taskListName, FireStoreClass().getCurrentUserID())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.add_list))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskListAfterChange(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.in_progress_text))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.in_progress_text))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = mBoardDetails.name
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }


    }

    fun updateTaskListSuccess() {
        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.in_progress_text))

        FireStoreClass().getBoardDetails(this, mBoardDetails.documentId)


    }

    fun addCardToTaskList(position: Int, cardName: String) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUserList: ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FireStoreClass().getCurrentUserID())

        val card = Card(cardName, FireStoreClass().getCurrentUserID(), cardAssignedUserList)
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)


        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )
        mBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.in_progress_text))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERs_REQUEST_CODE)
                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }
// This function will be used to get the list of users

    fun boardMemberDetailsList(list: ArrayList<User>) {
        mAssignedMemberDetailsList = list
        hideProgressDialog()

    }

    companion object {

        const val MEMBERs_REQUEST_CODE: Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }


}