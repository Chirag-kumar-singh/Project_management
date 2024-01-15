package com.projemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.projemanag.activities.MyProfileActivity

// TODO (Step 8.7: Create a constants object and make an users collection constant value.)

object Constants {

    //This is used for the collection name for USERS.
    const val USERS: String = "users"

    // TODO (Step 17.8: Add the Firebase database field names as constants.)
    // Firebase database field names
    const val IMAGE: String = "image"

    const val NAME: String = "name"

    const val MOBILE: String = "mobile"


    // TODO (Step 21.7: Add constant variable for Boards.)
    // This  is used for the collection name for USERS.
    const val BOARDS: String = "boards"

    // TODO (Step 24.3: Add a field name as assignedTo which we are gonna use later on.)
    const val ASSIGNED_TO : String = "assignedTo"


    // TODO (Step 20.2: Move all constants and function here and make them public and change accordingly.)
    // START
    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
    const val READ_STORAGE_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2

    // TODO (Step 26.3: Add constant for DocumentId)
    const val DOCUMENT_ID : String = "document"

    // TODO (Step 28.8: Add a new field for TaskList.)
    const val TASK_LIST: String = "taskList"

    // TODO (Step 33.1: Add constant for passing the board details through intent.)
    const val BOARD_DETAIL: String = "board_detail"

    // TODO (Step 34.3: Add field name as a constant which we will be using for getting the list of user details from the database.)
    const val ID: String = "id"

    const val EMAIL: String = "email"


    // TODO (Step 16.7: Create a function for image selection from phone storage.)
    // START
    /**
     * A function for user profile image selection from phone storage.
     */
     fun showImageChooser(activity: Activity){
        //An intent for launching the image selection of phone storage.
        var galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // Launches the image selection of phone storage using the constant code
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    // TODO (Step 17.2: Create a function to get the extension of the selected image.)
    /**
     * A function to get the extension of selected image.
     */
    fun getFileExtension(activity: Activity, uri: Uri?):String?{

        /*
        MimeTypeMap: Two way map that maps MIME-type to its file extensions and vice versa.

        getSingleton(): Get the singleton instance of Mimetype

        getExtensionFromMimeType: Return the registered extension for given MIME type.

        contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


}