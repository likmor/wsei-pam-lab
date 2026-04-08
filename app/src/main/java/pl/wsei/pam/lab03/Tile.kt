package pl.wsei.pam.lab03

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import java.util.Random

data class Tile(val button: ImageButton, val tileResource: Int, val deckResource: Int) {
    init {
        button.setImageResource(deckResource)
    }

    private var _revealed: Boolean = false
    var revealed: Boolean
        get() {
            return _revealed
        }
        set(value) {
            _revealed = value
            if (_revealed) button.setImageResource(tileResource) else button.setImageResource(
                deckResource
            )
        }

    fun removeOnClickListener() {
        button.setOnClickListener(null)
    }

    fun setAlpha(alpha: Float) {
        button.alpha = alpha
    }

    fun playMatchAnimation(action: Runnable) {
        animatePairedButton(button, action)
    }

    fun playNoMatchAnimation(action: Runnable) {
        animateNoPairButton(button, action)
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()
        val random = Random()
        button.pivotX = random.nextFloat() * 200f
        button.pivotY = random.nextFloat() * 200f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scallingX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scallingY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
        set.startDelay = 300
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scallingX, scallingY, fade)
        set.addListener(object : AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
                action.run();
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }

    private fun animateNoPairButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 30f)
        val rotation2 = ObjectAnimator.ofFloat(button, "rotation", -30f)
        val rotation3 = ObjectAnimator.ofFloat(button, "rotation", 0f)

        set.startDelay = 100
        set.duration = 220
        set.interpolator = DecelerateInterpolator()
        set.playSequentially(rotation, rotation2, rotation3)
//        set.playTogether(rotation)
        set.addListener(object : AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                revealed = false
                action.run()
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }
}