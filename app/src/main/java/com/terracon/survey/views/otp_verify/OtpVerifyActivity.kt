package com.terracon.survey.views.otp_verify

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.terracon.survey.R
import com.terracon.survey.databinding.OtpVerifyActivityBinding
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
    }

    private fun setupUi() {
        val value = intent.getStringExtra("mobile")

        binding.otpSentText.text = getString(
            R.string.otp_sent_msg,
            "+91 $value"
        )
        spannableStringWithColor(
            binding.otpSentText,
            26,
            binding.otpSentText.text.length,
            getString(R.string.otp_sent_msg,"+91 $value"),
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
           Toast.makeText(this,"OTP sent",Toast.LENGTH_SHORT).show()
        }
        binding.verifyOtpBtn.setOnClickListener {
            otpVerifyViewModel.navigateToHome(this@OtpVerifyActivity)
        }

    }
}
