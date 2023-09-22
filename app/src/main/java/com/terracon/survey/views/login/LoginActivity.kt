package com.terracon.survey.views.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.terracon.survey.R
import com.terracon.survey.databinding.LoginActivityBinding
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.AppUtils.spannableStringWithColor
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.views.register.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private val loginViewModel by viewModel<LoginViewModel>()
    private lateinit var binding: LoginActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()
        setupObservers()
        askNotificationPermission()
        getFcmToken()
    }

    private fun setupUi() {
        spannableStringWithColor(
            binding.registerNowText,
            23,
            31,
            getString(R.string.register_now_txt),
            R.color.colorAccent
        )

        binding.registerNowText.setOnClickListener {
            val homeIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(homeIntent)
        }

        binding.loginBtn.setOnClickListener {
            if (binding.mobileEditText.text.toString() != "" && AppUtils.isValidMobile(binding.mobileEditText.text.toString())) {
                loginViewModel.loginUser(binding.mobileEditText.text.toString(), this)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.invalid_mobile_number_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun setupObservers() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.loginBtn.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.loginBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            val msg = token
            Log.d("FCM Token", msg)
            // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }
}
