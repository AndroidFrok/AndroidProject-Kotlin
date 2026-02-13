package com.hjq.copy.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户实体类
 */
@Entity(tableName = "user")
data class User(

    /**
     * 用户名（主键）
     */
    @PrimaryKey
    @ColumnInfo(name = "username")
    val username: String,

    /**
     * 明文密码
     */
    @ColumnInfo(name = "password")
    val password: String
)
