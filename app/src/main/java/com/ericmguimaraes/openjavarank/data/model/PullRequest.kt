package com.ericmguimaraes.openjavarank.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import com.google.gson.annotations.SerializedName

@Entity(tableName = "prs",
        indices = [
            Index("id"),
            Index("user_login")],
        primaryKeys = ["id", "user_login"]
)
data class PullRequest(
        @SerializedName("id") val id: Int,
        @SerializedName("state") var state: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("body") var body: String = "",
        @SerializedName("html_url") var htmlUrl: String = "",
        @field:SerializedName("user")
        @field:Embedded(prefix = "user_")
        val user: User,
        var repo: String
) {
    data class User(
            @field:SerializedName("login")
            val login: String,
            @field:SerializedName("avatar_url")
            val aravatUrl: String?
    )

    companion object {
        const val UNKNOWN_ID = -1
    }
}