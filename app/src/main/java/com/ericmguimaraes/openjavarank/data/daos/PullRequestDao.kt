package com.ericmguimaraes.openjavarank.data.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ericmguimaraes.openjavarank.data.model.PullRequest

@Dao
interface PullRequestDao {

    @Query("SELECT * FROM prs")
    fun getPullRequests(): LiveData<List<PullRequest>>

    @Query("SELECT * FROM prs WHERE id = :id")
    fun getPullRequest(id: String): LiveData<PullRequest>

    @Query("SELECT * FROM prs WHERE repo = :repo")
    fun getPullRequests(repo: String): LiveData<List<PullRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(prs: List<PullRequest>)

}
