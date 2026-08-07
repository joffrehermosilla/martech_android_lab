package com.adobe.marketing.mobile.messagingsample.ui.logs

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adobe.marketing.mobile.messagingsample.R
import com.adobe.marketing.mobile.messagingsample.logger.LogItem

class TimelineAdapter(
    private var logs: List<LogItem> = emptyList()
) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorIndicator: View = view.findViewById(R.id.colorIndicator)
        val txtModule: TextView = view.findViewById(R.id.txtModule)
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }

    fun updateLogs(newLogs: List<LogItem>) {
        logs = newLogs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.txtModule.text = "[${log.level}] ${log.module}"
        holder.txtMessage.text = log.message
        holder.txtTime.text = log.time

        val indicatorColor = when (log.level.uppercase()) {
            "ERROR" -> Color.parseColor("#FF3B30")
            "WARNING", "WARN" -> Color.parseColor("#FA5A28")
            "SUCCESS" -> Color.parseColor("#1DB954")
            "DEBUG" -> Color.parseColor("#8E8E93")
            else -> Color.parseColor("#0057FF") // INFO
        }
        holder.colorIndicator.setBackgroundColor(indicatorColor)
    }
}