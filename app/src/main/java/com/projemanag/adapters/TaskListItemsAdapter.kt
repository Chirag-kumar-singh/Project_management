package com.projemanag.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.R
import com.projemanag.activities.TaskListActivity
import com.projemanag.models.Card
import com.projemanag.models.Task
import kotlinx.android.synthetic.main.item_task.view.*

// TODO (Step 27.5: Create an adapter class for Task List Items in the TaskListActivity.)
class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file.
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // TODO (Step 27.6: Here we have done some additional changes to display the item of the task list item in 70% of the screen size.)

        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        //Here the layout params are converted dynamically according to the screen size as width 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )

        //Here dynamically margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML layout files.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size - 1){
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_item.visibility = View.GONE
            }else{
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }

            // TODO (Step 28.5: Add a click event for showing the view for adding the task list name. And also set the task list title name.)
            holder.itemView.tv_task_list_title.text = model.title
            holder.itemView.tv_add_task_list.setOnClickListener{
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
            }

            // TODO (Step 28.6: Add a click event for hiding the view for adding the task list name.)
            holder.itemView.ib_close_list_name.setOnClickListener{
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE
            }


            // TODO (Step 28.11: Add a click event for passing the task list name to the base activity function. To create a task list.)
            holder.itemView.ib_done_list_name.setOnClickListener {

                val listName = holder.itemView.et_task_list_name.text.toString()

                if(listName.isNotEmpty()){
                    // Here we check the context is an instance of the TaskListActivity.
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context,
                    "please enter list name",
                    Toast.LENGTH_SHORT).show()
                }
            }

            // TODO (Step 29.1: Add a click event for iv_edit_list for showing the editable view.)

            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_task_list_name.setText(model.title)
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
            }

            // TODO (Step 29.2: Add a click event for iv_close_editable_view for hiding the editable view.)
            holder.itemView.ib_close_editable_view.setOnClickListener{
                holder.itemView.ll_title_view.visibility = View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility = View.GONE
            }

            // TODO (Step 29.4: Add a click event for iv_edit_list for showing thr editable view.)
            holder.itemView.ib_done_edit_list_name.setOnClickListener {

                val listName = holder.itemView.et_edit_task_list_name.text.toString()

                if(listName.isNotEmpty()){
                    // Here we check the context is an instance of the TaskListActivity.
                    if(context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                }else{
                    Toast.makeText(context,
                        "please enter list name",
                        Toast.LENGTH_SHORT).show()
                }
            }

            // TODO (Step 29.7: Add a click event for ib_delete_list for deleting the task list.)
            holder.itemView.ib_delete_list.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            // TODO (Step 30.3: Add a click event for adding a card in the task list.)
            holder.itemView.tv_add_card.setOnClickListener{
                holder.itemView.tv_add_card.visibility = View.GONE
                holder.itemView.cv_add_card.visibility = View.VISIBLE
            }

            // TODO (Step 30.4: Add a click event for closing the view for card add in the task list.)
            holder.itemView.ib_close_card_name.setOnClickListener {
                holder.itemView.tv_add_card.visibility = View.VISIBLE
                holder.itemView.cv_add_card.visibility = View.GONE
            }

            // TODO (Step 30.6: Add a click event for adding a card in the task list.)
            holder.itemView.ib_done_card_name.setOnClickListener {

                val cardName = holder.itemView.et_card_name.text.toString()

                if(cardName.isNotEmpty()){
                    // Here we check the context is an instance of the TaskListActivity.
                    if(context is TaskListActivity){
                        context.addCardToTaskList(position, cardName)
                    }
                }else{
                    Toast.makeText(context,
                        "please enter a card name",
                        Toast.LENGTH_SHORT).show()
                }
            }

            // TODO (Step 31.4: Load the cards list in the recyclerView.)
            holder.itemView.rv_card_list.layoutManager = LinearLayoutManager(context)
            holder.itemView.rv_card_list.setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context, model.cards)
            holder.itemView.rv_card_list.adapter = adapter

        }
    }

    // TODO (Step 29.6: Create a function to show an alert dialog for deleting the task list.)
    // START
    /**
     * Method is used to show the Alert Dialog for deleting the task list.
     */
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
    // END


    /**
     * A function to get density pixel from pixel
     */
    private fun Int.toDp(): Int=
        (this/Resources.getSystem().displayMetrics.density).toInt()


    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int=
        (this*Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)
}

