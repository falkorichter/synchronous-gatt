package com.sensorberg.synchronousgatt.sample

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import no.nordicsemi.android.support.v18.scanner.ScanResult

class Adapter(private val callback: (ScanResult) -> Unit) : ListAdapter<ScanResult, Holder>(diff) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)
		return Holder(callback, view)
	}

	override fun onBindViewHolder(holder: Holder, position: Int) {
		val data = getItem(position)!!
		holder.data = data
		holder.title.text = data.device.address
		holder.subtitle.text = "${data.rssi} dB"
	}

	companion object {
		private val diff = object : DiffUtil.ItemCallback<ScanResult>() {
			override fun areItemsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
				return oldItem.device.address == newItem.device.address
			}

			override fun areContentsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
				return oldItem.rssi == newItem.rssi
			}
		}
	}

}

class Holder(private val callback: (ScanResult) -> Unit, itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
	val title: TextView = itemView.findViewById(R.id.title)
	val subtitle: TextView = itemView.findViewById(R.id.subtitle)
	lateinit var data: ScanResult

	init {
		itemView.setOnClickListener { callback.invoke(data) }
	}
}