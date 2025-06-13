// SplashActivity.kt
package com.apkmob.mixit

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.apkmob.mixit.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ustawienie przezroczystości tekstu na start
        binding.textView.alpha = 0f

        startAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000) // Skrócony czas trwania animacji
    }

    private fun startAnimations() {
        // Animacja shakera
        val shaker = binding.shakerImage
        val textView = binding.textView

        // 1. Fade in shakera z lekkim przesunięciem w górę
        val fadeIn = ObjectAnimator.ofFloat(shaker, View.ALPHA, 0f, 1f).apply {
            duration = 600
        }
        val translateUp = ObjectAnimator.ofFloat(shaker, View.TRANSLATION_Y, 100f, 0f).apply {
            duration = 600
        }

        // 2. Animacja shakera (mieszania) z OvershootInterpolator dla bardziej naturalnego ruchu
        val rotateRight = ObjectAnimator.ofFloat(shaker, View.ROTATION, 0f, 25f).apply {
            duration = 200
            interpolator = OvershootInterpolator(0.5f)
        }

        val rotateLeft = ObjectAnimator.ofFloat(shaker, View.ROTATION, 25f, -15f).apply {
            duration = 200
            interpolator = OvershootInterpolator(0.5f)
        }

        val rotateBack = ObjectAnimator.ofFloat(shaker, View.ROTATION, -15f, 5f).apply {
            duration = 150
        }

        val stabilize = ObjectAnimator.ofFloat(shaker, View.ROTATION, 5f, 0f).apply {
            duration = 150
            interpolator = AccelerateDecelerateInterpolator()
        }

        // 3. Animacja tekstu - fade in z lekkim opóźnieniem
        val textFadeIn = ObjectAnimator.ofFloat(textView, View.ALPHA, 0f, 1f).apply {
            startDelay = 400
            duration = 600
        }
        val textScaleX = ObjectAnimator.ofFloat(textView, View.SCALE_X, 0.9f, 1f).apply {
            startDelay = 400
            duration = 600
        }
        val textScaleY = ObjectAnimator.ofFloat(textView, View.SCALE_Y, 0.9f, 1f).apply {
            startDelay = 400
            duration = 600
        }

        // Uruchomienie wszystkich animacji
        val animatorSet = AnimatorSet().apply {
            play(fadeIn).with(translateUp)
            play(rotateRight).after(fadeIn)
            play(rotateLeft).after(rotateRight)
            play(rotateBack).after(rotateLeft)
            play(stabilize).after(rotateBack)
            play(textFadeIn).with(textScaleX).with(textScaleY).after(rotateRight)
            start()
        }
    }
}