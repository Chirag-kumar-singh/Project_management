package com.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.adapters.BoardItemsAdapter
import com.projemanag.firebase.FireStoreClass
import com.projemanag.models.Board
import com.projemanag.models.User
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_add_subjects.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

// TODO (Step 12.6: Implement the NavigationView.OnNavigationItemSelectedListener and add the implement members of it.)
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    // TODO (Step 18.1: Create a companion object and a constant variable for My profile Screen result.)
    /**
     * A companion object to declare the constants.
     */
    companion object{
        //A unique code for starting the activity for result.
        const val MY_PROFILE_REQUEST_CODE : Int = 11

        // TODO (Step 25.1: Add a unique code for starting the create board activity for result)
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    private lateinit var mUserName: String

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)

        // This is used to align the xml view to this class
        setContentView(R.layout.activity_main)

        // TODO (Step 12.4: Call the setup action bar function here.)
        setupActionBar()

        // TODO (Step 12.8: Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.)
        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)

        // TODO (Step 13.3: Call a function to get the current logged in user details.)
        // START
        // Get the current logged in user details.
        FireStoreClass().loadUserdata(this@MainActivity, true)

        // TODO (Step 19.9: Launch the Create Board screen on a fab button click.)
        fab_create_board.setOnClickListener{
            // TODO (Step 21.4: Pass the user name through intent to CreateBoardScreen.)
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)

            // TODO (Step 25.2: Here now pass the unique code for StartActivityForResult.)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }


    // TODO (Step 24.1: Create a function to populate the result of BOARDS list in the UI i.e in the recyclerView.)
    /**
     * A function to populate the result of BOARDS list in the UI i.e in the recyclerView.
     */
    fun populateBoardListToUI(boardsList: ArrayList<Board>){
        hideProgressDialog()

        if(boardsList.size > 0){
            rv_boards_list.visibility = View.VISIBLE
            tv_no_boards_available.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)


            // Create an instance of BoardItemsAdapter and pass the boardList to it.
            val adapter = BoardItemsAdapter(this, boardsList)
            rv_boards_list.adapter = adapter    //Attach the adapter to the recyclerview.

            // TODO (Step 28.9: Add click event for boards item and launch the TaskListActivity)
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {

                    // TODO (Step 26.4: Pass the documentId of a board through intent.)
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            rv_boards_list.visibility = View.GONE
            tv_no_boards_available.visibility = View.VISIBLE
        }


    }

    // TODO (Step 12.1: Create a function to setup action bar.)
    // START
    /**
     * A function to setup action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        // TODO (Step 12.3: Add click event for navigation in the action bar and call the toggleDrawer function.)

        toolbar_main_activity.setNavigationOnClickListener{
            //Toggle drawer
            toggleDrawer()
        }
    }

    // TODO (Step 12.5: Add a onBackPressed function and check if the navigation drawer is open or closed.)
    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            doubleBacktoExit()
        }
    }

    // TODO (Step 12.2: Create a function for opening and closing the Navigation Drawer.)
    // START
    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer(){
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    // TODO (Step 13.5: Create a function to update the user details in the navigation view.)
    // START
    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User, readBoardList: Boolean){

        // TODO (Step 21.3: Initialize the UserName variable.)
        mUserName = user.name

        //Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image)   //URL of the image
            .centerCrop()       //Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder)   //A default place holder.
            .into(nav_user_iamge)   //the view in which the image will be loaded.

        tv_username.text = user.name

        if(readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardList(this)

        }
    }

    // TODO (Step 18.4: Add the onActivityResult function and check the result of the activity for which we expect the result.)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE){
            //Get the user updated details.
            FireStoreClass().loadUserdata(this)

            // TODO (Step 25.4: Here if the result is OK get the updated boards list.)
        }else if(resultCode == Activity.RESULT_OK &&
                requestCode == CREATE_BOARD_REQUEST_CODE){
            FireStoreClass().getBoardList(this)
        }
        else{
            Log.e("Cancelled", "Cancelled")
        }

    }

    // TODO (Step 12.7: Implement members of NavigationView.OnNavigationItemSelectedListener.)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // TODO (Step 12.9: Add the click events of navigation menu items.)
        when(item.itemId){
            R.id.nav_my_profile ->{
                //Toast.makeText(this@MainActivity, "My profile", Toast.LENGTH_SHORT).show()
                // TODO (Step 14.9: Launch the MyProfileActivity Screen.)
                // TODO (Step 18.2: Launch the my profile activity for Result.)
                startActivityForResult(Intent(this
                    , MyProfileActivity::class.java)
                    , MY_PROFILE_REQUEST_CODE)

            }
            R.id.nav_sign_out -> {
                //Here sign out user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                //sends the user to intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.attendance -> {
                startActivity(Intent(this, Add_subjects::class.java))
            }


        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
