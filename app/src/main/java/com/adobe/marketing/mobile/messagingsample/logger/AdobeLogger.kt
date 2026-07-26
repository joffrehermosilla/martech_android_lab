package com.adobe.marketing.mobile.messagingsample.logger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LogItem(
    val time: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
    val module: String,
    val level: String = "INFO",
    val message: String
)

object AdobeLogger {

    private val _logs = MutableLiveData<List<LogItem>>(emptyList())
    val logsLiveData: LiveData<List<LogItem>> get() = _logs

    private val logList = mutableListOf<LogItem>()

    @Synchronized
    fun add(module: String, message: String, level: String = "INFO") {
        val item = LogItem(
            module = module,
            level = level.uppercase(Locale.getDefault()),
            message = message
        )
        logList.add(0, item)
        _logs.postValue(ArrayList(logList))
    }

    @Synchronized
    fun getLogs(): List<LogItem> {
        return ArrayList(logList)
    }

    @Synchronized
    fun clear() {
        logList.clear()
        _logs.postValue(emptyList())
    }
}
