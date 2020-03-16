package com.nrxus.calllog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import kotlinx.android.synthetic.main.fragment_call_entry.view.*

class CallEntryViewAdapter : RecyclerView.Adapter<CallEntryViewAdapter.ViewHolder>() {

    companion object {
        val DATE_FORMAT: DateFormat =
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    }

    private val entries = mutableListOf<CallLog.Entry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_call_entry, parent, false)
            .let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = entries[position]
        holder.number.text = item.number
        holder.timestamp.text = DATE_FORMAT.format(item.time)
        holder.direction.text = when (item.direction) {
            CallLog.Direction.Incoming -> "INCOMING"
            CallLog.Direction.Outgoing -> "OUTGOING"
        }
    }

    override fun getItemCount(): Int = entries.size

    fun setData(callLog: CallLog) {

        val diff = DiffUtil.calculateDiff(CallLogDiffCallback(entries, callLog.entries))

        entries.clear()
        entries.addAll(callLog.entries)

        diff.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.number
        val timestamp: TextView = view.timestamp
        val direction: TextView = view.direction
    }
}
