package com.ericmguimaraes.openjavarank.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import com.google.gson.annotations.SerializedName

/**
 * Using name/owner_login as primary key instead of id since name/owner_login is always available
 * vs id is not.
 */
@Entity(tableName = "repos",
        indices = [
            Index("id"),
            Index("owner_login")],
        primaryKeys = ["name", "owner_login"]
)
data class Repo(
        val id: Int,
        @field:SerializedName("name")
        val name: String,
        @field:SerializedName("full_name")
        val fullName: String,
        @field:SerializedName("description")
        val description: String?,
        @field:SerializedName("owner")
        @field:Embedded(prefix = "owner_")
        val owner: Owner,
        @field:SerializedName("stargazers_count")
        val stars: Int,
        @field:SerializedName("forks_count")
        val forks: Int
) {

    data class Owner(
            @field:SerializedName("login")
            val login: String,
            @field:SerializedName("url")
            val url: String?,
            @field:SerializedName("avatar_url")
            val aravatUrl: String?
    )

    companion object {
        const val UNKNOWN_ID = -1
    }
}
