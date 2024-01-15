package com.projemanag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.RenderProcessGoneDetail
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projemanag.R
import com.projemanag.firebase.FireStoreClass
import com.projemanag.models.User
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.IOException
import java.net.URI
import java.util.jar.Manifest

// TODO (Step 14.1: Create a MyProfileActivity class.)

class MyProfileActivity : BaseActivity() {

    // TODO(Step 20.1: Move the list of Items below to Constants file so we can re use them in the CreateBoardActivity
    // 1. Function showImageChooser().
    // 2. Function getFileExtension(uri: Uri?).
    // 3. Constants of Companion Object for Selecting image form phone storage and Read Storage Permission


//    // TODO (Step 16.3: Create a companion object and add a constant for Read Storage runtime permission.)
//    // START
//    /**
//     * A companion object to declare the constants.
//     */
//    companion object {
//        //A unique code for asking the Read storage permission using this we will be check and identify in the method onRequestPermissionResult
//        private const val READ_STORAGE_PERMISSION_CODE = 1
//
//        // TODO (Step 16.6: Add a constant for image selection from phone storage)
//        private const val PICK_IMAGE_REQUEST_CODE = 2
//    }

    // TODO (Step 16.10: Add a global variable for URI of a selected image from phone storage.)
    // START
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for user details.
    private var mProfileImageURL : String = ""

    // TODO (Step 17.6: Add the global variables for UserDetails and Profile Image URL.)
    // A global variable for user details.
    private lateinit var mUserDetail: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        // TODO (Step 14.8: Call a function to setup action bar.)
        setupActionBar()

        // TODO (Step 15.1: Call a function to get the current logged in user details.)
        FireStoreClass().loadUserdata(this@MyProfileActivity)

        // TODO (Step 16.4: Add a click event for iv_profile_user_image.)
        iv_profile_user_image.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
                // TODO (Step 16.8: Call the image chooser function.)
                Constants.showImageChooser(this)

            }else{
                /*
                Requests permissions to be granted to this application. These permissions
                must be requested in your manifest, they should not be granted to your app,
                and they should have protection level.
                 */
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        // TODO (Step 17.10: Add a click event for updating the user profile data to the database.)
        btn_update.setOnClickListener {

            //Here if the image is not selected then update the other details of user.
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString((R.string.please_wait)))

                //Call a function to update user details in the database
                updateUserProfileData()
            }
        }
    }


    // TODO (Step 16.5: Check the result of runtime permission after the user allows or deny based on the unique code.)
    // START
    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
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
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // TODO(Step 16.9: Call the image chooser function.)
                Constants.showImageChooser(this)
            } else {

                //Displaying another toast if permission is not granted.
                Toast.makeText(
                    this,
                    "Oops , you just denied the permission for storage, allow it form settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

//    // TODO (Step 16.7: Create a function for image selection from phone storage.)
//    // START
//    /**
//     * A function for user profile image selection from phone storage.
//     */
//    private fun showImageChooser(){
//        //An intent for launching the image selection of phone storage.
//        var galleryIntent = Intent(Intent.ACTION_PICK,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//
//        // Launches the image selection of phone storage using the constant code
//        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
//    }

    // TODO (Step 16.11: Get the result of the image selection based on the constant code.)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            //The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                //Load the user image in the ImageView.
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)   //URL of the image
                    .centerCrop()       //Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder)   //A default place holder.
                    .into(iv_profile_user_image)   //the view in which the image will be loaded.
            }catch (e: IOException){
                e.printStackTrace()
            }
        }


    }

    // TODO (Step 14.7: Create a function to setup action bar.)
    // START
    /**
     * A function to setup action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // TODO (Step 15.2: Create a function to set the existing data in UI.)
    // START
    /**
     * A function to set the existing details in UI.
     */
    fun setUserDataUserUI(user: User){

        // TODO(Step 17.7: Initialize the user details variable.)
        // Initialize the user details variable.
        mUserDetail = user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)   //URL of the image
            .centerCrop()       //Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder)   //A default place holder.
            .into(iv_profile_user_image)   //the view in which the image will be loaded.

        et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.mobile != 0L) {
            Log.d("MyProfileActivity", "Mobile number is not zero: ${user.mobile}")
            et_mobile.setText(user.mobile.toString())
        } else {
            Log.d("MyProfileActivity", "Mobile number is zero or not available.")
        }
    }

    // TODO (Step 17.9: Update the user profile details into the database.)
    /**
     * A function to update the user profile details into the database.
     */
    private fun updateUserProfileData(){
        var userHashMap = HashMap<String, Any>()

        var anyChangesMade = false
        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetail.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMade = true
        }

        if(et_name.text.toString() != mUserDetail.name){
            userHashMap[Constants.NAME] = et_name.text.toString()
            anyChangesMade  = true
        }

        if(et_mobile.text.toString() != mUserDetail.mobile.toString()){
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()
            anyChangesMade = true
        }

        //Update the data in the database.
        if(anyChangesMade)
        FireStoreClass().updateUserProfileData(this, userHashMap)
    }

    //TODO(Step 17.3: Create a function to upload the selected user image to storage and get the url of it to store in the database.)
    //START
    //Before start with database we need to perform some steps in Firebase Console and after adding a dependency in Gradle file.
    //Step 1: Go to the storage tab in the Firebase Console in your project details int the navigation bar under "Develop"
    //Step 2: In the storage page click on get started. Click on Next.
    //Step 3: As we have already selected the storage location while creating the database so now click on the Done button.
    //Step 4: Now the storage bucket is created.
    //Step 5: For more details visit the link: https://firebase.google.com/docs/storage/android/start
    // Step 6: Now add the code to upload image.
    /**
     * A function to upload the selected user image to firebase cloud storage.
     */
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting the storage reference
        if(mSelectedImageFileUri != null){
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE" + System.currentTimeMillis()
            + "." + Constants.getFileExtension(this, mSelectedImageFileUri))

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                //image upload is success
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                //Get the downloadable url from the task snapshot.
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener{
                    uri->
                    Log.i("Downloadable Image URI", uri.toString())
                    //assign the image url to variable.
                    mProfileImageURL = uri.toString()

                    //call a function to update user details in the database.
                    hideProgressDialog()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_SHORT
                ).show()

                hideProgressDialog()
            }
        }
    }



    // TODO (Step 17.4: Create a function to notify the user profile is updated successfully.)
    /**
     * A function to notify the user profile is updated successfully.
     */
    fun profileUpdateSuccess(){
        hideProgressDialog()

        // TODO (Step 18.3: Send the success result to the Base Activity.)
        setResult(Activity.RESULT_OK)
        finish()
    }

}