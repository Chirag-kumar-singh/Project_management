package com.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projemanag.R
import com.projemanag.firebase.FireStoreClass
import com.projemanag.models.Board
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_create_board.*
import java.io.IOException

// TODO (Step 19.2: Create a CreateBoardActivity.)
class CreateBoardActivity : BaseActivity() {

    // TODO (Step 20.3: Add a global variable for URI of a selected image from phone storage.)
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // TODO (Step 21.5: Create a global variable for User name)
    private lateinit var mUserName: String

    // TODO (Step 22.1: Create a global variable for the Board image URL.)
    // A global variable for a board image URL
    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        // TODO (Step 19.8: Call the setup action bar function)
        setupActionBar()

        // TODO (Step 21.6: Get the username from the intent.)
        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME)
        }

        // TODO (Step 20.4: Add click event for iv_board_image.)
        iv_board_image.setOnClickListener { view ->

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@CreateBoardActivity)
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

        // TODO (Step 22.4: Add a click event for btn_create.)
        btn_create.setOnClickListener {

            //Here if the image is not selected then update the other details of user.
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))

                //Call a function to update create a board.
                createBoard()
            }
        }
    }

    // TODO (Step 20.5: Here the read storage permission result will be handled. And further execution will be done.)
    // START
    /**
     * This function will notify the user after tapping on allow or deny
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@CreateBoardActivity)
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
    // END

    // TODO (Step 20.6: Get the result of the image selection based on the constant code.)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data

            try {
                // Load the board image in the ImageView.
                Glide
                    .with(this@CreateBoardActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(iv_board_image) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // TODO (Step 22.2: Create a function to create the board.)
    /**
     * A function to make an entry of a board in the database.
     */
    private fun createBoard(){

        // A list is created to add the assigned members.
        // This can be modified later on as of now the user itself will be the member of the board.
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())  // Adding the current used id.


        // Creating the instance of the board and adding the values as per parameters.
        var board = Board(
            et_board_name.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FireStoreClass().createBoard(this, board)
    }

    // TODO (Step 22.3: Creating the function to upload the Board Image to storage and getting the downloadable URL of the image.)
    /**
     * A function to upload the Board Image to storage and getting the downloadable URL of the image.
     */
    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting the storage reference

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "BOARD_IMAGE" + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this, mSelectedImageFileUri)
        )

        //adding the file to reference
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
            //image upload is success
            Log.i(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            //Get the downloadable url from the task snapshot.
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Downloadable Image URI", uri.toString())
                //assign the image url to variable.
                mBoardImageURL = uri.toString()

                createBoard()
            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(
                this@CreateBoardActivity,
                exception.message,
                Toast.LENGTH_SHORT
            ).show()

            hideProgressDialog()
        }
    }


    // TODO (Step 21.8: Create a function which will notify the success of board creation.)
    /**
     * A function for notifying the board is created successfully.
     */
    fun boardCreatedSuccessfully(){
        hideProgressDialog()

        // TODO (Step 25.3: Set the result as OK.)
        setResult(Activity.RESULT_OK)
        finish()
    }



    // TODO (Step 19.7: Create a function to setup action bar.)
    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_create_board_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_create_board_activity.setNavigationOnClickListener { onBackPressed() }
    }
}