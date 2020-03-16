package com.nrxus.calllog

import androidx.recyclerview.widget.DiffUtil

class CallLogDiffCallback(
    private val old: List<CallLog.Entry>,
    private val new: List<CallLog.Entry>
) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size
    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        old[oldItemPosition] == new[newItemPosition]

    // assumes contents of the same item never change
    // only called if areItemsTheSame returned true
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
}
