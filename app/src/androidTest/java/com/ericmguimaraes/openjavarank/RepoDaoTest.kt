package com.ericmguimaraes.openjavarank

import android.support.test.runner.AndroidJUnit4
import com.ericmguimaraes.openjavarank.data.model.Repo
import com.ericmguimaraes.openjavarank.utils.LiveDataTestUtil.getValue
import com.ericmguimaraes.openjavarank.utils.TestUtil
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepoDaoTest : DbTest() {
    @Test
    fun insertAndRead() {

        val repo = TestUtil.createRepo(123,"foo", "bar", "desc")
        val list = ArrayList<Repo>()
        list.add(repo)
        db.repoDao().insertAll(list)
        val loaded = getValue(db.repoDao().getRepo(123))
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.name, CoreMatchers.`is`("bar"))
        MatcherAssert.assertThat(loaded.description, CoreMatchers.`is`("desc"))
        MatcherAssert.assertThat(loaded.owner, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.owner.login, CoreMatchers.`is`("foo"))
    }
}