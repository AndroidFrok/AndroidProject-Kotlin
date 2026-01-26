package com.ex.serialport.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ex.serialport.R

class LogListAdapter(list: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_layout,
    list as MutableList<String>?
) {

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.textView, item)
    }

    fun clean() {
        data.clear()
        notifyDataSetChanged()
    }
}
