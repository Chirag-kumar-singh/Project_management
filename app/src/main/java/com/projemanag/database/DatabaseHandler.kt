package com.projemanag.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.projemanag.models.Subject

class DatabaseHandler(context: Context) :
SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SubjectDatabase"
        private const val TABLE_SUBJECT_DETAIL = "SubjectTable"

        private const val KEY_ID = "_id"
        private const val SUBJECT_NAME = "subject_name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_SUBJECT_TABLE = ("CREATE TABLE " + TABLE_SUBJECT_DETAIL + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + SUBJECT_NAME + " TEXT)")
        db?.execSQL(CREATE_SUBJECT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECT_DETAIL")
        onCreate(db)
    }

    /**
     * Function to insert a Subject detail details to SQLite Database.
     */

    fun addSubjectDetail(SubjectDetail: Subject): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(SUBJECT_NAME, SubjectDetail.Subject_name)
        // Inserting Row
        val result = db.insert(TABLE_SUBJECT_DETAIL, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return result
    }

    fun getSubjectlist(): ArrayList<Subject>{
        val subjectList: ArrayList<Subject> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_SUBJECT_DETAIL" // Database select query

        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        val subject = Subject(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(SUBJECT_NAME))
                        )
                        subjectList.add(subject)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
        } catch (e:SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        finally {
            db.close()
        }
        return subjectList
    }
}