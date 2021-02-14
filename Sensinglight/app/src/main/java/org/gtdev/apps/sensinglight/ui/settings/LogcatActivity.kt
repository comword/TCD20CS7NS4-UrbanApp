package org.gtdev.apps.sensinglight.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.gtdev.apps.sensinglight.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class LogcatActivity : AppCompatActivity() {
    private var cBuffer: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logcat)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        title = "Logcat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val fab = findViewById<View>(R.id.LogcatCopy) as FloatingActionButton
        fab.setOnClickListener { view ->
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Logcat", cBuffer)
            clipboard.setPrimaryClip(clip)
            Snackbar.make(view, R.string.msg_log_copied, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        try {
            val process = Runtime.getRuntime().exec("logcat -d")
            val bufferedReader = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            val log = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                log.append(line)
                log.append("\n")
            }
            val tv = findViewById<View>(R.id.textView_logcat) as TextView
            tv.movementMethod = ScrollingMovementMethod()
            tv.text = log.toString()
            cBuffer = log.toString()
            //final int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
            //tv.scrollTo(0, scrollAmount);
            val scrollview = findViewById<View>(R.id.scrollViewLogCat) as ScrollView
            scrollview.viewTreeObserver.addOnGlobalLayoutListener {
                scrollview.post {
                    scrollview.fullScroll(
                        View.FOCUS_DOWN
                    )
                }
            }
        } catch (e: IOException) {
        }
    }
}