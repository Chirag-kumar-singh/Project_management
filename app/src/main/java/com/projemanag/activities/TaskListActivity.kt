package com.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.icu.text.Transliterator.Position
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call.Details
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentId
import com.projemanag.R
import com.projemanag.adapters.TaskListItemsAdapter
import com.projemanag.firebase.FireStoreClass
import com.projemanag.models.Board
import com.projemanag.models.Card
import com.projemanag.models.Task
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_task_list.*

// TODO (Step 25.5: Create a TaskListActivity.)
class TaskListActivity : BaseActivity() {
    // TODO (Step 28.2: Create a global variable for Board Details.)
    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board

    private lateinit var mBoardDocumentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // TODO (Step 26.5: Get the board documentId through intent.)
        //var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)
        }

        // TODO (Step 26.10: Call the function to get the Board Details.)
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, mBoardDocumentId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardDetails(this, mBoardDocumentId)
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }

    // TODO (Step 32.7: Inflate the action menu for TaskListScreen and also launch the MembersActivity Screen on item selection.)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate the menu to use in the action bar.
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Handle the presses on tha action bar menu items.
        when(item.itemId){
            R.id.action_members -> {

                // TODO (Step 33.2: Pass the board details through intent.)
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)

                // TODO (Step 37.2: Start activity for result.)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // TODO (Step 26.7: Create a function to get the result of Board Detail.)
    /**
     * A function to get the result of Board Detail.
     */
    fun boardDetails(board: Board) {

        // TODO (Step 3: Initialize and Assign the value to the global variable for Board Details.
        //  After replace the parameter variable with global so from onwards the global variable will be used.)
        mBoardDetails = board
        hideProgressDialog()

        // TODO (Step 26.8: call the setup actionbar function.)
        // Call the function to setup action bar.
        // TODO (Step 28.4: Remove the parameter and add the title from global variable in the setupActionBar function.)
        // Call the function to setup action bar.
        setupActionBar()

        //Here we are appending an item view for adding a list task list for the board.
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)


        rv_task_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rv_task_list.setHasFixedSize(true)

        //create an instance of TaskListItemsAdapter and pass the task list to it.
        val adapter = TaskListItemsAdapter(this, board.taskList)
        rv_task_list.adapter = adapter  //Attach the adapter to the recyclerview.
    }

    // TODO (Step 28.7: Create a function to get the result of add or updating the task list.)
    /**
     * A function to get the result of add or updating the task list.
     */
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        // Here get the updated board details.
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    // TODO (Step 26.6: Create a function to setup action bar.)
    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {
        setSupportActionBar(toolbar_task_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name

            toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
        }
    }

    // TODO (Step 28.10: Create a function to get the task list name from the adapter class which we will be using to create a new task list in the database.)

    /**
     * A function to get the task list name from the adapter class which we will be using to create a new task list in the database.
     */
    fun createTaskList(taskListName: String){

        //Create and assign the task details.
        val task = Task(taskListName, FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task) // Add task to the first position of arraylist

        // Remove the last position as we have added the item manually for adding the tasklist.
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    // TODO (Step 29.3: Create a function to update the taskList.)
    /**
     * A function to update the taskList
     */
    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size  -1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    // TODO (Step 29.5: Create a function to delete the task list.)
    /**
     * A function to delete the task list from database.
     */
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    // TODO (Step 30.5: A function to create a card and update it in the task list.)
    /**
     * A function to create a card and update it in the task list.
     */
    fun addCardToTaskList(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUserList: ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FireStoreClass().getCurrentUserId())

        val card = Card(cardName, FireStoreClass().getCurrentUserId(), cardAssignedUserList)

        val cardList = mBoardDetails.taskList[position].cards
        cardList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )

        mBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)


    }

    // TODO (Step 37.1: Create a companion object and declare a constant for starting an MembersActivity for result.)
    /**
     * A companion object to declare the constants.
     */
    companion object{
        //A unique code for stating the activity for result.
        const val MEMBERS_REQUEST_CODE: Int = 13
    }

}