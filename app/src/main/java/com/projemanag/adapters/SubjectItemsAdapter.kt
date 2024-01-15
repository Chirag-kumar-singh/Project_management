package com.projemanag.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.R
import com.projemanag.activities.MainActivity
import com.projemanag.models.Subject
import kotlinx.android.synthetic.main.item_subject.view.*

open class SubjectItemsAdapter
    (private val context: Context,
private var list: ArrayList<Subject>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            holder.itemView.subject_name.text = model.Subject_name

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Subject)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

}