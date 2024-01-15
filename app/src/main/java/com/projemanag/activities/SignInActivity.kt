package com.projemanag.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.firebase.FireStoreClass
import com.projemanag.models.User
import kotlinx.android.synthetic.main.activity_sign_in.*

// TODO (Step 4.1: Add the Sign In activity.)
// START
class SignInActivity : BaseActivity() {


    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_sign_in)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // TODO (Step 4.8: Call the setup actionBar function.)
        setupActionBar()

        // TODO(Step 7.4: Add click event for sign-in button and call the function to sign in.)

        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }
    }

    // TODO (Step 9.4: Create a function to get the user details from the firestore database after authentication.)
    // START
    /**
     * A function to get the user details from the firestore database after authentication.
     */
    fun signInSuccess(user: User){
        hideProgressDialog()

        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()

    }

    // TODO (Step 4.7: A function for setting up the actionBar.)
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // TODO (Step 7.2: A function for Sign-In using the registered user using the email and password.)
    // START
    /**
     * A function for Sign-In using the registered user using the email and password.
     */
    private fun signInRegisteredUser(){
        val email: String = et_email_signin.text.toString().trim {it <= ' '}
        val password: String = et_password_signin.text.toString().trim { it <= ' ' }

        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            // Sign-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {

//                        Toast.makeText(
//                            this@SignInActivity,
//                            "You have successfully signed in.",
//                            Toast.LENGTH_LONG
//                        ).show()

                        // TODO (Step 9.2: Remove the toast message and call the FirestoreClass signInUser function to get the data of user from database. And also move the code of hiding Progress Dialog and Launching MainActivity to Success function.)
                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FireStoreClass().loadUserdata(this@SignInActivity)
                        // END

                        } else {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }

    // TODO (Step 7.3: A function to validate the entries of a user.)
    // START
    /**
     * A function to validate the entries of a user.
     */
    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }
    // END
}
// END