package com.projemanag.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.R
import com.projemanag.adapters.SubjectItemsAdapter
import com.projemanag.database.DatabaseHandler
import com.projemanag.models.Board
import com.projemanag.models.Subject
import kotlinx.android.synthetic.main.activity_add_subjects.*
import kotlinx.android.synthetic.main.activity_create_board.*

class Add_subjects : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subjects)

        setupActionBar()

        fab_add_subject.setOnClickListener {
            startActivity(Intent(this, CreateSubjectActivity::class.java))
        }

        getSubjectListFromLocalDB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ADD_SUBJECT_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getSubjectListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or back pressed")
            }
        }
    }

//    fun populateSubjectsListToUI(SubjectsList: ArrayList<Subject>){
//        hideProgressDialog()
//
//        if(SubjectsList.size > 0){
//            rv_subjects_list.visibility = View.VISIBLE
//            tv_no_subjects_available.visibility = View.GONE
//
//            rv_subjects_list.layoutManager = LinearLayoutManager(this)
//            rv_subjects_list.setHasFixedSize(true)
//
//            val adapter = SubjectItemsAdapter(this, SubjectsList)
//            rv_subjects_list.adapter = adapter
//        }else{
//            rv_subjects_list.visibility = View.GONE
//            tv_no_subjects_available.visibility = View.VISIBLE
//        }
//    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_subjects_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Subjects"
        }

        toolbar_subjects_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getSubjectListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)

        val getSubjectList: ArrayList<Subject> = dbHandler.getSubjectlist()

        if(getSubjectList.size > 0){
            rv_subjects_list.visibility = View.VISIBLE
            tv_no_subjects_available.visibility = View.GONE
            setupSubjectRecyclerView(getSubjectList)
        }else{
            rv_subjects_list.visibility = View.GONE
            tv_no_subjects_available.visibility = View.VISIBLE
        }
    }

    private fun setupSubjectRecyclerView(SubjectsList: ArrayList<Subject>){
        rv_subjects_list.layoutManager = LinearLayoutManager(this)
        rv_subjects_list.setHasFixedSize(true)

        val subjectAdapter = SubjectItemsAdapter(this, SubjectsList)
        rv_subjects_list.adapter = subjectAdapter
    }

    companion object{
        var ADD_SUBJECT_ACTIVITY_REQUEST_CODE = 1;
    }


}