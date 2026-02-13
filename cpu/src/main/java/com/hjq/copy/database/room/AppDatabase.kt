package com.hjq.copy.database.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * 应用数据库
 */
@Database(
    entities = [User::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 获取用户 DAO
     */
    abstract fun userDao(): UserDao

    companion object {
        /**
         * 数据库名称
         */
        const val DATABASE_NAME = "app_database"
    }
}
