package com.projemanag.models

import android.os.Parcel
import android.os.Parcelable

// TODO (Step 26.1: Create a data model class for Task.)
data class Task (
    var title: String = "",
    val createdBy: String = "",

    // TODO (Step 30.2: Add one more parameter as a cards list using the card model class.)
    val cards: ArrayList<Card> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(p0: Parcel, p1: Int) = with(p0) {
        writeString(title)
        writeString(createdBy)
        writeTypedList(cards)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}