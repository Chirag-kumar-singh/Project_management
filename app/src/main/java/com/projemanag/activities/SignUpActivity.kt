package com.projemanag.activities

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.projemanag.R
import com.projemanag.firebase.FireStoreClass
import com.projemanag.models.User
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_sign_up)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
    }


    // TODO (Step 8.8: Create a function to be called the user is registered successfully and entry is made in the firestore database.)
    // START
    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess(){
        Toast.makeText(this,
        "You have " + "successfully registered", Toast.LENGTH_SHORT).show()

        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        //Finish the SignUp Screen
        finish()
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }

        // TODO (Step 5.11: Add a click event to the Sign-Up button and call the registerUser function.)
        // START
        // Click event for sign-up button.
        btn_sign_up.setOnClickListener{
            registerUser()
        }
    }

    // TODO (Step 5.9: A function to register a new user to the app.)
    // START
    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser(){
        val name: String = et_name.text.toString().trim() { it <= ' '}
        val email: String = et_email.text.toString().trim() { it <= ' '}
        val password: String = et_password.text.toString().trim() { it <= ' '}

        if(validateForm(name, email, password)){
//            Toast.makeText(
//                this@SignUpActivity,
//                "Now we can register a new user",
//                Toast.LENGTH_SHORT
//            ).show()

            // TODO (Step 6.1 : Here we will register a new user using firebase and we will see the entry in Firebase console.)
            // START
            // Before doing this you need to perform some steps in the Firebase Console.
            // 1. Go to your project detail.
            // 2. Click on the "Authentication" tab which is on the left side in the navigation bar under the "Develop" section.
            // 3. In the Authentication Page, you will see the tab named “Sign-in method”. Click on it.
            // 4. In the sign-in providers, enable the “Email/Password”.
            // 5. Finally, Now you will be able to Register a new user using the Firebase.

            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                // TODO (Step 8.9: Move hide progressbar function in the userRegisteredSuccess function.)
                //hide the progess dialog
                //hideProgressDialog()

                //if registration is successfully done
                if (task.isSuccessful) {

                    //Firebase registered user
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    //Registered Email
                    val registeredEmail = firebaseUser.email!!
//                    Toast.makeText(
//                        this,
//                        "$name you have " +
//                                "successfully registered the email " +
//                                "address $registeredEmail", Toast.LENGTH_SHORT
//                    ).show()


                    // TODO(Step 8.1: As you can see we are now authenticated by Firebase but for more inserting more details we need to use the DATABASE in Firebase.)
                    // START
                    // Before start with database we need to perform some steps in Firebase Console and add a dependency in Gradle file.
                    // Follow the Steps:
                    // Step 1: Go to the "Database" tab in the Firebase Console in your project details in the navigation bar under "Develop".
                    // Step 2: In the Database Page and Click on the Create Database in the Cloud Firestore in the test mode. Click on Next
                    // Step 3: Select the Cloud Firestore location and press the Done.
                    // Step 4: Now the database is created in the test mode and now add the cloud firestore dependency.
                    // Step 5: For more details visit the link: https://firebase.google.com/docs/firestore
                    // END


                    // TODO (Step 8.4: Now here we will make an entry in the Database of a new user registered.)
                    // START
                    val user = User(firebaseUser.uid, name, registeredEmail)

                    //call the registerUser function of FirestoreClass to make an entry in the database.
                    FireStoreClass().registerUser(this, user)
                    //END

                    /**
                     * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
                     * and send him to Intro Screen for Sign-In
                     */

                    // TODO (Step 8.10: Move the activity finish line code in the success function.)

//                    FirebaseAuth.getInstance().signOut()
//
//                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // TODO (Step 5.10: A function to validate the entries of a new user.)
    // START
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String , password: String): Boolean{

        return when {
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email")
                false
            }TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }else -> {
                true
            }
        }
    }
}