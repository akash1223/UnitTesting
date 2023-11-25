package com.example.lysnclient.model

import androidx.lifecycle.MutableLiveData

data class WBTSelectedAnswer(
    val quePosition: Int,
    var answerLabel: MutableLiveData<String>,
    var mWBTSeekBarValue: Int,
    var faceTypeImgId: MutableLiveData<Int>,
    var backgroundImgId: MutableLiveData<Int>,
    var backgroundColorId: MutableLiveData<Int>,
    val value: String
)
