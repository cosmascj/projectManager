package com.example.domoreeveryday.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.domoreeveryday.R
import com.example.domoreeveryday.firebase.FireStoreClass
import com.example.domoreeveryday.model.Board
import com.example.domoreeveryday.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        toolbar = findViewById(R.id.toolbar_task_list_activity)


        var boardDocumentId : String= ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            showProgressDialog(resources.getString(R.string.in_progress_text))
            FireStoreClass().getBoardDetails(this, boardDocumentId)
        }
    }

    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)

    }

    private fun setupActionBar(title : String){
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null){

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = title
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }


    }
}