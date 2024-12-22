package com.hjq.base

import android.content.Context

/**
 *  方便多个模块公用的上下文
 */
class CommonContext {
    companion object {
        private var ctx: Context? = null;
        public fun initContext(context: Context) {
            ctx = context.applicationContext
        }

        fun getContext(): Context {
            return ctx!!;

        }
    }
}