package com.hjq.copy.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {

    /**
     * 插入用户（如果主键冲突则替换）
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    /**
     * 插入多个用户（如果主键冲突则替换）
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    /**
     * 根据用户名查询用户（使用 Flow 返回，数据变化时会自动更新）
     */
    @Query("SELECT * FROM user WHERE username = :username")
    fun getUserByUsername(username: String): Flow<User?>

    /**
     * 根据用户名查询用户（同步方法）
     */
    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserByUsernameSync(username: String): User?

    /**
     * 查询所有用户
     */
    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    /**
     * 更新用户
     */
    @Update
    suspend fun update(user: User)

    /**
     * 删除用户
     */
    @Delete
    suspend fun delete(user: User)

    /**
     * 根据用户名删除用户
     */
    @Query("DELETE FROM user WHERE username = :username")
    suspend fun deleteByUsername(username: String)

    /**
     * 删除所有用户
     */
    @Query("DELETE FROM user")
    suspend fun deleteAll()

    /**
     * 获取用户数量
     */
    @Query("SELECT COUNT(*) FROM user")
    suspend fun getCount(): Int
}
