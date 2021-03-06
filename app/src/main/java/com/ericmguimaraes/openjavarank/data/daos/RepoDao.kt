package com.ericmguimaraes.openjavarank.data.daos

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ericmguimaraes.openjavarank.data.model.Repo

@Dao
interface RepoDao {

    @Query("SELECT * FROM repos ORDER BY stars desc")
    fun getRepos(): DataSource.Factory<Int, Repo>

    @Query("SELECT * FROM repos WHERE id = :repoId")
    fun getRepo(repoId: Int): LiveData<Repo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repos: List<Repo>)

    @Query("SELECT COUNT(id) FROM repos")
    fun getNumberOfRows(): Int

}
