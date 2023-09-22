package com.terracon.survey.views.otp_verify

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.terracon.survey.R
import com.terracon.survey.databinding.OtpVerifyActivityBinding
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.AppUtils.spannableStringWithColor
import org.koin.androidx.viewmodel.ext.android.viewModel


class OtpVerifyActivity : AppCompatActivity() {

    private val otpVerifyViewModel by viewModel<OtpVerifyViewModel>()
    private lateinit var binding: OtpVerifyActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OtpVerifyActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()
        setupObservers()
    }

    private fun setupUi() {
        val mobileNumber = intent.getStringExtra("mobile")

        binding.otpSentText.text = getString(
            R.string.otp_sent_msg,
            "+91 $mobileNumber"
        )
        spannableStringWithColor(
            binding.otpSentText,
            26,
            binding.otpSentText.text.length,
            getString(R.string.otp_sent_msg,"+91 $mobileNumber"),
            R.color.colorAccent
        )
        spannableStringWithColor(
            binding.otpResend,
            23,
            binding.otpResend.text.length,
            getString(R.string.otp_resend_msg),
            R.color.colorAccent
        )

        binding.otpResend.setOnClickListener {
            if (mobileNumber != null) {
                otpVerifyViewModel.resendOtp(mobileNumber,this)
            }
        }
        binding.verifyOtpBtn.setOnClickListener {
            if (binding.otpInput.editText?.text.toString() != "" && binding.otpInput.editText?.text.toString().length == 6) {
                if (mobileNumber != null) {
                    otpVerifyViewModel.loginUser(mobileNumber,binding.otpInput.editText?.text.toString(), this)
                }else{
                    Toast.makeText(
                        this,
                        getString(R.string.invalid_otp_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.invalid_otp_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
           // otpVerifyViewModel.loginUser(mobileNumber,,this@OtpVerifyActivity)
        }

    }

    private fun setupObservers() {
        otpVerifyViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.verifyOtpBtn.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.verifyOtpBtn.visibility = View.VISIBLE
            }
        }
    }
}
