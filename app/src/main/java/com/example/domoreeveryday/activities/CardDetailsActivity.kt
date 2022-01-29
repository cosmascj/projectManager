package com.example.domoreeveryday.activities

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domoreeveryday.R
import com.example.domoreeveryday.adapters.CardMemberListItemsAdapter
import com.example.domoreeveryday.adapters.LabelColorListAdapter
import com.example.domoreeveryday.dialogs.LabelColorListDialog
import com.example.domoreeveryday.dialogs.MembersListDialog
import com.example.domoreeveryday.firebase.FireStoreClass
import com.example.domoreeveryday.model.*
import com.example.domoreeveryday.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var tool_bar: Toolbar
    private var mCardPosition = -1
    private var mTaskPosition = -1
    private lateinit var mBoardDetails: Board
    private lateinit var et_card_details: TextView
    private lateinit var btn_update: Button
    private var mSelectedColor: String = ""
    private lateinit var tv_select_label_color: TextView
    private lateinit var mMembersDetailList: ArrayList<User>
    private lateinit var tv_selectmembers: TextView
    private lateinit var rv_selected_members_list: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        tool_bar = findViewById(R.id.toolbar_card_details_activity)
        et_card_details = findViewById(R.id.et_name_card_details)
        btn_update = findViewById(R.id.btn_update_card_details)
        tv_select_label_color = findViewById(R.id.tv_select_label_color)
        tv_selectmembers = findViewById(R.id.tv_select_members)
        rv_selected_members_list = findViewById(R.id.rv_selected_members_list)
        getIntentData()
        setUpActionBar()
        et_card_details.text = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name
        //et_card_details.setText(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)

        mSelectedColor = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }
        btn_update.setOnClickListener {
            if (et_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            }
        }

        tv_selectmembers.setOnClickListener {
            membersListDialog()
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }
        setUpSelectedMembersList()
    }


    private fun setUpActionBar() {
        setSupportActionBar(tool_bar)
        var actionBar = actionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name

        }
        tool_bar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_icon -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!


        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }

        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }

        /**
         * Use getParcebleArrayListExtre to get the an array list being sent across with an intent
         * */

        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }

    }

    fun updateTaskListSuccess() {

        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()

    }

    private fun updateCardDetails() {
        val card = Card(
            et_card_details.text.toString(),
            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo, mSelectedColor


        )
        val taskList: ArrayList<Task> =mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)


        mBoardDetails.taskList[mTaskPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.in_progress_text))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    private fun deleteCards() {
        val cardList: ArrayList<Card> = mBoardDetails.taskList[mTaskPosition].cards
        cardList.removeAt(mCardPosition)
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskPosition].cards = cardList

        showProgressDialog(resources.getString(R.string.in_progress_text))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    private fun membersListDialog(){

        val cardAssignedMemberList = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition]
            .assignedTo

        if(cardAssignedMemberList.size > 0){
            for(i in mMembersDetailList.indices){
                for(j in cardAssignedMemberList){
                    if (mMembersDetailList[i].id == j){
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }else{
            for (i in mMembersDetailList.indices){
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            mMembersDetailList,resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        user.id?.let {
                            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo.add(
                                it
                            )
                        }
                    }
                    else{
                        mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo.remove(user.id)

                        for (i in mMembersDetailList.indices){
                            if (mMembersDetailList[i].id == user.id){
                                mMembersDetailList[i].selected = false
                            }
                        }
                    }
                    setUpSelectedMembersList()

                }

            }

        }
        listDialog.show()
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteCards()
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A function to remove the text and set the label color to the TextView.
     */
    private fun setColor() {
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }


    // TODO (Step 6: Create a function to add some static label colors in the list.)

    /**
     * A function to add some static label colors in the list.
     */
    private fun colorsList(): ArrayList<String> {

        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }


    // TODO (Step 10: Create a function to launch the label color list dialog.)
    // START
    /**
     * A function to launch the label color list dialog.
     */
    private fun labelColorsListDialog() {

        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorListDialog(
            this@CardDetailsActivity,
            colorsList,
            resources.getString(R.string.str_select_label_color), mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    fun setUpSelectedMembersList(){
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition]
            .assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices){
            for (j in cardAssignedMemberList){
                if (mMembersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id!!,
                        mMembersDetailList[i].image!!
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            tv_selectmembers.visibility= View.GONE
            rv_selected_members_list.visibility= View.VISIBLE
            rv_selected_members_list.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMemberListItemsAdapter(this, selectedMembersList)
            rv_selected_members_list.adapter = adapter
            adapter.setOnClickListener(
                object :CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }

                }
            )

        }else{
            tv_selectmembers.visibility =View.VISIBLE
            rv_selected_members_list.visibility = View.GONE
        }
    }
}