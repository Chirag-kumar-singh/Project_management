package com.projemanag.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projemanag.R
import com.projemanag.database.DatabaseHandler
import com.projemanag.models.Subject
import kotlinx.android.synthetic.main.activity_create_subject.*

class CreateSubjectActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_subject)
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_subject_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "New Subject"
        }

        btn_add.setOnClickListener(this)

        toolbar_create_subject_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_add -> {
                when{
                    et_subject_name.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter name of subject", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }

            }

            }

    }

//    fun subjectCreatedSuccessfully(){
//        hideProgressDialog()
//        finish()
//    }

//    private fun createSubject(){
//        var subject = subject(
//            et_subject_name.text.toString()
//        )
//    }
}