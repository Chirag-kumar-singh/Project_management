package com.projemanag.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.R
import com.projemanag.adapters.MemberListItemsAdapter
import com.projemanag.firebase.FireStoreClass
import com.projemanag.models.Board
import com.projemanag.models.User
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*

// TODO (Step 32.1: Create a MembersActivity.)
class MembersActivity : BaseActivity() {

    // TODO (Step 33.3: Create a global variable for Board Details.)
    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board

    // TODO (Step 36.1: A global variable for Users List.)
    // A global variable for Assigned Members List.
    private lateinit var mAssignedMembersList: ArrayList<User>

    private var anyChangeMade: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        //TODO(Step 33.4: Get the Board Details through intent and assign it to the global variable.)
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)

        }

        //TODO(Step 33.6 - Call the setup action bar function.)
        setupActionBar()


        // TODO (Step 34.5: Get the members list details from the database.)
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)

    }

    // TODO (Step 34.2: Create a function to setup assigned members list into recyclerview.)
    /**
     * A function to setup assigned members list into recyclerview.
     */
    fun setUpMembersList(list: ArrayList<User>){

        // TODO (Step 36.2: Initialize the Assigned Members List.)
        mAssignedMembersList = list

        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        rv_members_list.adapter = adapter
    }

    // TODO (Step 36.3: Here we will get the result of the member if it found in the database.)
    fun memberDetails(user: User){
        // TODO (Step 36.6: Here add the user id to the existing assigned members list of the board.)
        mBoardDetails.assignedTo.add(user.id)

        // TODO (Step 36.9: Finally assign the member to the board.)
        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    // TODO (Step 33.5: Create a function to setup action bar.)
    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // TODO (Step 35.3: Inflate the menu file for adding the member and also add the onOptionItemSelected function.)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Handle presses on the action bar menu items.
        when(item.itemId){
            R.id.action_add_member -> {

                // TODO (Step 35.7: Call the dialogSearchMember function here.)
                dialogSearchMember()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // TODO (Step 35.6: Initialize the dialog for searching member from Database.)
    /**
     * Method is used to show the Custom Dialog.
     */
    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        /*
        Set the screen content form a layout resource.
        The resource will be inflated, adding all top level view to the screen.
         */
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener{
            val email = dialog.et_email_search_member.text.toString()

            if(email.isNotEmpty()){
                dialog.dismiss()

                // TODO (Step 36.5: Get the member details from the database.)
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this, email)
            }else{
                Toast.makeText(
                    this@MembersActivity,
                    "please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangeMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    // TODO (Step 36.7: Initialize the dialog for searching member from Database.)
    /**
     * A function to get the result of assigning the members.
     */
    fun memberAssignedSuccess(user: User){
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangeMade = true

        setUpMembersList(mAssignedMembersList)
    }
}