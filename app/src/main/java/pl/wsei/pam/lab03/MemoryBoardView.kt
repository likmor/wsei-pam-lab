package pl.wsei.pam.lab03

import android.util.Log
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
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))

            it.shuffle()
        }

        // tu umieść kod pętli tworzący wszystkie karty, który jest obecnie
        // w aktywności Lab03Activity
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

                    val tile = addTile(it, shuffledIcons.removeAt(0))
                }


            }
        }
    }

    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { (e) -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
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

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }
}