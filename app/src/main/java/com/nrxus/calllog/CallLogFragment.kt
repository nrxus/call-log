package com.nrxus.calllog

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CallLogFragment : Fragment() {
    companion object {
        const val REAL_CALL_PERMISSION_REQUEST = 1
        fun newInstance() = CallLogFragment()
    }

    private lateinit var adapter: CallEntryViewAdapter
    private val viewModel: CallLogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = CallEntryViewAdapter()

        val view = inflater.inflate(R.layout.fragment_call_log, container, false)
            .let { it as RecyclerView }
            .also {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = adapter
            }

        viewModel.callLog.observe(viewLifecycleOwner, Observer { callLog ->
            when (callLog) {
                is Result.Err -> {
                    this.requestPermissions(
                        arrayOf(permission.READ_CALL_LOG),
                        REAL_CALL_PERMISSION_REQUEST
                    )
                }
                is Result.Ok -> {
                    adapter.setData(callLog.v)
                    view.smoothScrollToPosition(0)
                }
            }
        })

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REAL_CALL_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.permissionsGranted()
            } else {
                Toast.makeText(
                    context,
                    "Could not get Call Logs",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
