package pl.wsei.pam.lab03

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.math.log

class Lab03Activity : AppCompatActivity() {
    lateinit var mBoard: GridLayout
    lateinit var completionPlayer: MediaPlayer
    lateinit var negativePLayer: MediaPlayer
    lateinit var mBoardModel: MemoryBoardView
    var isSound = true
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

        val col = size[0]
        val row = size[1]


        mBoard.columnCount = col
        mBoard.rowCount = row

        mBoardModel = MemoryBoardView(mBoard, col, row)
        if (savedInstanceState != null) {
            val state = savedInstanceState.getIntArray("state")
            mBoardModel.setState(state)
        }
        mBoardModel.setOnGameChangeListener { e ->
            run {
                when (e.state) {
                    GameStates.Matching -> {
                        e.tiles.forEach { it.revealed = true }
                    }

                    GameStates.Match -> {
                        if (isSound) completionPlayer.start()
                        mBoardModel.isLocked = true

                        e.tiles.forEach {
                            it.revealed = true

                            it.playMatchAnimation { mBoardModel.isLocked = false }
                        }
                    }

                    GameStates.NoMatch -> {
                        if (isSound) negativePLayer.start()

                        mBoardModel.isLocked = true

                        e.tiles.forEach {
                            it.revealed = true
                            it.playNoMatchAnimation {
                                mBoardModel.isLocked = false
                                it.revealed = false
                            }

                        }

                    }

                    GameStates.Finished -> {
                        if (isSound) completionPlayer.start()
                        e.tiles.forEach {
                            it.revealed = true;
                            it.playMatchAnimation { mBoardModel.isLocked = false }
                        }
                        Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override protected fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePLayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }


    override protected fun onPause() {
        super.onPause();
        completionPlayer.release()
        negativePLayer.release()
    }

    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("state", mBoardModel.getState().toIntArray())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.board_activity_sound -> {
                if (isSound) {
                    Toast.makeText(this, "Sound turn off", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.speaker_muted)
                    isSound = false
                } else {
                    Toast.makeText(this, "Sound turn on", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.speaker_icon)
                    isSound = true
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}