package com.daimajia.numberprogressbar.example

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.numberprogressbar.NumberProgressBar
import com.daimajia.numberprogressbar.OnProgressBarListener
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity(), OnProgressBarListener {
    private var timer: Timer? = null

    private var bnp: NumberProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bnp = findViewById<NumberProgressBar>(R.id.numberbar1)
        bnp?.setOnProgressBarListener(this)
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread(object : Runnable {
                    override fun run() {
                        bnp?.incrementProgressBy(1)
                    }
                })
            }
        }, 1000, 100)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    override fun onProgressChange(current: Int, max: Int) {
        if (current == max) {
            Toast.makeText(applicationContext, getString(R.string.finish), Toast.LENGTH_SHORT)
                .show()
            bnp?.setProgress(0)
        }
    }
}
