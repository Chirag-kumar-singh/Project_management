package com.projemanag.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import kotlinx.android.synthetic.main.dialog_progress.*

// TODO (Step 5.4: Here we have created a BaseActivity Class in which we have added the progress dialog and SnackBar. Now all the activity will extend the BaseActivity instead of AppCompatActivity.)
// START
open class BaseActivity : AppCompatActivity() {

    /**
     * This is a progress dialog instance which we will initialize later on.
     */
    private var doubleBackToExitPressesOnce = false

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.tv_progress_text.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun getCurrentUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBacktoExit() {
        if (doubleBackToExitPressesOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressesOnce = true
        Toast.makeText(
            this,
            "Please click back again to exit.",
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({doubleBackToExitPressesOnce = false},2000)

    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)

        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }
}