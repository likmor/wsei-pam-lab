package pl.wsei.pam.lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab02)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favorites_grid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun test(v: View) {
        val tag: String? = v.tag as String?
        val tokens: List<String>? = tag?.split(" ")
        val rows = tokens?.get(0)?.toInt()
        val cols = tokens?.get(1)?.toInt()
        val intent = Intent(this, Lab03Activity::class.java)
        val size: IntArray = intArrayOf(rows ?: 0, cols ?: 0)
        intent.putExtra("size", size)
        startActivity(intent)

    }
}