package com.inmoment.moments.framework.datamodel

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
data class CollectionsResponseData(
    @SerializedName("data")
    val data: Data
) : Parcelable {

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Data(
        @SerializedName("collections")
        val collections: MutableList<CollectionModel>?
    ) : Parcelable
}

@SuppressLint("ParcelCreator")
@Parcelize
data class CollectionOperationResponseData(
    @SerializedName("data")
    val data: Data
) : Parcelable {

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Data(
        @SerializedName(value = "createCollection",alternate = ["updateCollection","deleteCollection"])
        val collections: CollectionModel
    ) : Parcelable
}

@SuppressLint("ParcelCreator")
@Parcelize
data class CollectionModel(
    @SerializedName("collectionType")
    val collectionType: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("records")
    val records: MutableList<String>,
    @SerializedName("datasource")
    val dataSource: DataSource,
    @SerializedName("sortOrder")
    val sortOrder: String,
    @SerializedName("__typename")
    val typename: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    data class DataSource(
        @SerializedName("name")
        val dataSourceName: String
    ) : Parcelable
}