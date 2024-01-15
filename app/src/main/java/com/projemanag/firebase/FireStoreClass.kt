package com.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projemanag.activities.*
import com.projemanag.models.Board
import com.projemanag.models.User
import com.projemanag.utils.Constants
import javax.crypto.BadPaddingException

// TODO (Step 8.3: Create a class where we will add the operation performed for the firestore database.)
// START
/**
 * A custom class where we will add the operation performed for the firestore database.
 */

class FireStoreClass {

    //Create an instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()


    // TODO (Step 8.5: Create a function to make an entry of the registered user in the firestore database.)
    /**
     * A function to make an entry of the registered user in the firestore database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: User){

        mFireStore.collection(Constants.USERS)
            // Document ID for users field. Here the document it is the User ID.
            .document(getCurrentUserId())
            // Here the userInfo are Field and the setOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                //Here call a function of base activity in SignUpActivity for transferring the result to it.
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName, "Error")
            }
    }

    // TODO (Step 26.9: Create a function to get the Board Details.)
    /**
     * A function to get the Board Details.
     */
    fun getBoardDetails(activity: TaskListActivity, documentId: String){

        //The collection name
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                // TODO (Step 28.1: Assign the board document id to the Board Detail object)
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id

                //send the result of board to the base activity
                activity.boardDetails(board)
            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating board")
            }
    }

    // TODO (Step 21.9: Create a function for creating a board and making an entry in the database.)
    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully.")

                Toast.makeText(activity, "Board created successfully", Toast.LENGTH_SHORT).show()

                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while creating a board.",
                exception)

            }
    }

    // TODO (Step 24.4: Create a function to get the list of created boards from the database.)
    /**
     * A function to get the list of created boards from the database.
     */
    fun getBoardList(activity: MainActivity){

        //The collection name
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }

                activity.populateBoardListToUI(boardList)
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating board")
            }
    }

    // TODO (Step 28.9: Create a function to add the task list in the board detail.)
    /**
     * A function to create a task list in the board detail.
     */
    fun addUpdateTaskList(activity: TaskListActivity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully")

                activity.addUpdateTaskListSuccess()
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", exception)
            }
    }

    // TODO (Step 17.5: Create a function to update the user profile data into the database.)
    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                //Notify the success result.
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board",
                    e
                )
                Toast.makeText(activity, "Error while updating the profile!", Toast.LENGTH_SHORT).show()

            }
    }


    // TODO (Step 9.1: Create a function to SignIn using firebase and get the user details from Firestore Database.)
    // START
    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */

    // TODO (Step 13.1: We can use the same function to get the current logged in user details. As we need to modify only few things here.)
    // START
    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun loadUserdata(activity: Activity, readBoardList: Boolean = false){
        //Here we pass the collection name from which we want the data.
        mFireStore.collection(Constants.USERS)
            // Document ID for users field. Here the document it is the User ID.
            .document(getCurrentUserId())
            // Here the userInfo are Field and the setOption is set to merge. It is for if we wants to merge
            .get()
            .addOnSuccessListener {document ->

                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                // TODO (STEP 9.3: Pass the result to base activity.)
                // START
                // Here we have received the document snapshot which is converted into the User Data model object.

                // Debug log for mobile field
                val mobileValue = document["mobile"]
                Log.d(activity.javaClass.simpleName, "Mobile from Firestore: $mobileValue")

                val loggedInUser = document.toObject(User::class.java)!!

                // TODO(Step 13.6: Modify the parameter and check the instance of activity and send the success result to it.)
                // TODO(Step 15.3: Modify the parameter and check the instance of activity and send the success result to it.)
                // START
                // Here call a function of base activity for transferring the result to it.
                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                    }

                    is MyProfileActivity -> {
                        activity.setUserDataUserUI(loggedInUser)
                    }
                }
            }.addOnFailureListener {
                    e->

                // TODO(Step 13.2: Hide the progress dialog in failure function based on instance of activity.)
                // TODO(Step 15.4: Hide the progress dialog in failure function based on instance of activity.)
                // START
                // Here call a function of base activity for transferring the result to it.
                when(activity){
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error",e)
            }
    }

    // TODO (Step 8.6: Create a function for getting the user id of current logged user.)
    // START
    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserId(): String{
        //TODO (Step 10.1: Return the user id if he is already logged in before or else it will be blank.)
        //An Instance of currentUser using FirebaseAuth
        var currentUser = FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserId = ""
        if(currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    // TODO (Step 34.4: Create a function for getting the list of users details from the database.)
    /**
     * A function to get the list of user details which is assigned to the board.
     */
    fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList()

                for (i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                activity.setUpMembersList(usersList)
            }
            .addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    // TODO (Step 36.4: Create a function to get the user details from Firestore Database using the email address.)
    /**
     * A function to get the user details from Firestore Database using the email address.
     */
    fun getMemberDetails(activity: MembersActivity, email: String){

        //Here we pass the collection name form which we wants the data.
        mFireStore.collection(Constants.USERS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                document->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!

                    //Here call a function of base activity for transferring the result to it.
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }.addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    // TODO (Step 8: Create a function to assign a updated members list to board.)
    /**
     * A function to assign a updated members list to board.
     */
    fun assignMemberToBoard(
        activity: MembersActivity, board: Board, user: User){

        val assignedToHashmap = HashMap<String, Any>()
        assignedToHashmap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashmap)
            .addOnSuccessListener {
                activity.memberAssignedSuccess(user)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
}