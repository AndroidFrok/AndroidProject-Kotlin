package com.ex.serialport.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ex.serialport.R

class SpAdapter(private val mContext: Context) : BaseAdapter() {

    private var datas: Array<String>? = null

    fun setDatas(datas: Array<String>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return datas?.size ?: 0
    }

    override fun getItem(position: Int): Any? {
        return datas?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, null)
            holder = ViewHolder()
            holder.mTextView = view as TextView
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.mTextView.text = datas?.get(position) ?: ""
        return view
    }

    private class ViewHolder {
        lateinit var mTextView: TextView
    }
}
