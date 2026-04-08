package pl.wsei.pam.lab03

import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import pl.wsei.pam.lab01.R
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val deckResource: Int = R.drawable.deck
    public var isLocked = false

    private val icons: List<Int> = listOf(
        R.drawable.baseline_music_note_24,
        R.drawable.outline_airwave_24,
        R.drawable.outline_alarm_24,
        R.drawable.outline_airport_shuttle_24,
        R.drawable.outline_accessibility_24,
        R.drawable.outline_add_location_24,
        // dodaj kolejne identyfikatory utworzonych ikon
    )

    init {
        generateBoard()
    }

    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { (e) -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    private fun onClickTile(v: View) {
        if (isLocked) return
        val tile = tiles[v.tag] ?: return
        if (tile.revealed) return
        if (matchedPair.contains(tile)) return
        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource ?: -1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int, revealed: Boolean = false) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        if (revealed) tile.setAlpha(0f)
        tile.revealed = revealed
        tiles[button.tag.toString()] = tile

    }

    fun getState(): List<Int> {
        return tiles.map { (string, tile) -> if (tile.revealed) tile.tileResource else -1 }

    }

    fun setState(state: IntArray?) {
        gridLayout.removeAllViewsInLayout()
        generateBoard(state)
        var revealed = 0;
        if (state != null) {
            revealed = state.count { it != -1 }
        }
        logic.matches = revealed / 2
    }

    private fun generateBoard(state: IntArray? = null) {

        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))

            it.shuffle()
            if (state != null) {
                val revealedIcons = state.filter { it != -1 }
                it.removeAll { el -> el in revealedIcons }
            }
        }

        var stateIterator = 0

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val btn = ImageButton(gridLayout.context).also {
                    it.tag = "${r}x${c}"
                    val layoutParams = GridLayout.LayoutParams()
                    it.setImageResource(deckResource)
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(c, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(r, 1, 1f)
                    it.layoutParams = layoutParams
                    gridLayout.addView(it)

                    if (state == null) {
                        val tile = addTile(it, shuffledIcons.removeAt(0))
                    } else {
                        if (state[stateIterator] != -1) {
                            val tile = addTile(it, state[stateIterator], true)
                        } else {
                            val tile = addTile(it, shuffledIcons.removeAt(0))
                        }
                        stateIterator++
                    }

                }

            }
        }
    }

}

