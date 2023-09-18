package com.terracon.survey.views.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.terracon.survey.databinding.SplashActivityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : AppCompatActivity() {

    private val splashViewModel by viewModel<SplashViewModel>()
    private lateinit var binding: SplashActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        splashViewModel.checkIfUserLoggedIn(this)
    }
}
