package com.adobe.marketing.mobile.messagingsample.ui.logs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adobe.marketing.mobile.messagingsample.R
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger

class LogsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private val adapter = TimelineAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        supportActionBar?.title = "Adobe Timeline Logs"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler = findViewById(R.id.recyclerLogs)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        AdobeLogger.logsLiveData.observe(this) { logs ->
            adapter.updateLogs(logs)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateLogs(AdobeLogger.getLogs())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}