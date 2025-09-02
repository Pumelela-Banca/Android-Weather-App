package com.example.myapplication.domain


@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: Session)

    @Query("SELECT * FROM session_table WHERE id = 0")
    suspend fun getSession(): Session?

    @Query("DELETE FROM session_table")
    suspend fun clearSession()
}