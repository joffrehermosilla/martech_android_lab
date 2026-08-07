package com.adobe.marketing.mobile.messagingsample.ui.logs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.R
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Button
import android.widget.Toast

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
        val btnCopyLogs = findViewById<Button>(R.id.btnCopyLogs)
        val btnClearLogs = findViewById<Button>(R.id.btnClearLogs)

        btnCopyLogs.setOnClickListener {
            copyLogsToClipboard()
        }

        btnClearLogs.setOnClickListener {
            AdobeLogger.clear()
        }
    }

    private fun copyLogsToClipboard() {

        val logs = AdobeLogger.getLogs()

        if (logs.isEmpty()) {
            Toast.makeText(
                this,
                "No hay logs para copiar",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val text = logs
            .asReversed()
            .joinToString("\n\n") { log ->

                "${log.time} [${log.level}] ${log.module}\n${log.message}"
            }

        val clipboard =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText(
            "Adobe AJO Lab Logs",
            text
        )

        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            this,
            "${logs.size} logs copiados",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        // RASTREO DE CICLO DE VIDA
        MobileCore.lifecycleStart(null)
        adapter.updateLogs(AdobeLogger.getLogs())
    }

    override fun onPause() {
        super.onPause()
        MobileCore.lifecyclePause()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}