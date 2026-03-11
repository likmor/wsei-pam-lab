package pl.wsei.pam.lab03

import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    lateinit var mBoard: GridLayout
    lateinit var mBoardModel: MemoryBoardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memory_game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mBoard = findViewById(R.id.memory_game)
        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)

        val row = size[0]
        val col = size[1]

        mBoard.columnCount = col
        mBoard.rowCount = row

        mBoardModel = MemoryBoardView(mBoard, col, row)

        mBoardModel.setOnGameChangeListener { e ->
            run {
                when (e.state) {
                    GameStates.Matching -> {
                        e.tiles.forEach { it.revealed = true }
                    }

                    GameStates.Match -> {
                        e.tiles.forEach { it.revealed = true }

                    }

                    GameStates.NoMatch -> {
                        e.tiles.forEach {
                            it.revealed = true
                            Timer().schedule(2000) {
                                runOnUiThread {
                                    it.revealed = false
                                }
                            }
                        }

                    }

                    GameStates.Finished -> {
                        Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}