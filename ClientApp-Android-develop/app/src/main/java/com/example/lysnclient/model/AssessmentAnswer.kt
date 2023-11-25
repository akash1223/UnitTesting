package com.example.lysnclient.model

import android.os.Parcel
import android.os.Parcelable

class AssessmentAnswer(
    val questionType: String,
    val questionId: Int,
    val quePosition: Int,
    var questionLabel: String,
    var userAnswer: String,
    var singleChoiceOptionPosition: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questionType)
        parcel.writeInt(questionId)
        parcel.writeInt(quePosition)
        parcel.writeString(questionLabel)
        parcel.writeString(userAnswer)
        parcel.writeInt(singleChoiceOptionPosition)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssessmentAnswer> {
        override fun createFromParcel(parcel: Parcel): AssessmentAnswer {
            return AssessmentAnswer(parcel)
        }

        override fun newArray(size: Int): Array<AssessmentAnswer?> {
            return arrayOfNulls(size)
        }
    }
}
