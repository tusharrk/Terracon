package com.terracon.survey.views.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.terracon.survey.R
import com.terracon.survey.databinding.RegisterActivityBinding
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.AppUtils.spannableStringWithColor
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterActivity : AppCompatActivity() {

    private val registerViewModel by viewModel<RegisterViewModel>()
    private lateinit var binding: RegisterActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()
        setupObservers()
    }

    private fun setupUi() {
        spannableStringWithColor(
            binding.loginAgainText,
            25,
            30,
            getString(R.string.login_text_signup_page),
            R.color.colorAccent
        )
        binding.loginAgainText.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.signupBtn.setOnClickListener {
            if(binding.fullNameInput.editText?.text.toString() == ""){
                Toast.makeText(
                    this,
                    getString(R.string.invalid_name_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }else if(binding.fullNameInput.editText?.text.toString() == ""){
                Toast.makeText(
                    this,
                    getString(R.string.invalid_mobile_number_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
           else if (!AppUtils.isValidMobile(binding.mobileNumberInput.editText?.text.toString())) {
                Toast.makeText(
                    this,
                    getString(R.string.invalid_mobile_number_msg),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                registerViewModel.registerUser(binding.mobileNumberInput.editText?.text.toString(),binding.fullNameInput.editText?.text.toString(),this@RegisterActivity)
            }
        }

    }

    private fun setupObservers() {
        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.signupBtn.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.signupBtn.visibility = View.VISIBLE
            }
        }
    }
}
