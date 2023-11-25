package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName
import kotlin.collections.ArrayList

class Options(@SerializedName("choices") val listOfOptions: ArrayList<OptionType> = ArrayList()) {
}
