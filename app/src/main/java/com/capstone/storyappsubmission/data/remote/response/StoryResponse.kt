package com.capstone.storyappsubmission.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem> = emptyList(),

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class ListStoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Double
) : Parcelable

@Parcelize
data class StoryDetailResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("story")
	val story: ListStoryItem,

	@field:SerializedName("error")
	val error: Boolean
) : Parcelable