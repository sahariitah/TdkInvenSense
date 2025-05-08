

package com.example.tdkinvences

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.punchthrough.blestarterappandroid.hasRequiredBluetoothPermissions

class ScanResultAdapter(
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_scan_result,
            parent,
            false
        )
        return ViewHolder(view, onClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.ViewHolder(view) {

        @SuppressLint("MissingPermission", "SetTextI18n")
        fun bind(result: ScanResult) {
            view.findViewById<TextView>(R.id.device_name).text =
                if (view.context.hasRequiredBluetoothPermissions()) {
                    result.device.name ?: "Unnamed"
                } else {
                    error("Missing required Bluetooth permissions")
                }
            view.findViewById<TextView>(R.id.mac_address).text = result.device.address
            view.findViewById<TextView>(R.id.signal_strength).text = "${result.rssi} dBm"
            val fullSignalImage = view.findViewById<ImageView>(R.id.signal)
            val mediumSignalImage = view.findViewById<ImageView>(R.id.mediumsignal)
            val noSignalImage = view.findViewById<ImageView>(R.id.nosignal)

            when {
                result.rssi > -70 -> {
                    fullSignalImage.visibility = View.VISIBLE
                    mediumSignalImage.visibility = View.GONE
                    noSignalImage.visibility = View.GONE
                }
                result.rssi in -70..-90 -> {
                    fullSignalImage.visibility = View.GONE
                    mediumSignalImage.visibility = View.VISIBLE
                    noSignalImage.visibility = View.GONE
                }
                else -> {
                    fullSignalImage.visibility = View.GONE
                    mediumSignalImage.visibility = View.GONE
                    noSignalImage.visibility = View.VISIBLE
                }
            }

            view.setOnClickListener { onClickListener.invoke(result) }


        }
    }
}
