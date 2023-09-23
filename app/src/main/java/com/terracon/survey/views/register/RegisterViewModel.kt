package com.terracon.survey.views.register

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.michaelflisar.lumberjack.L
import com.terracon.survey.R
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.Result
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.utils.Prefs
import com.terracon.survey.views.login.LoginActivity
import com.terracon.survey.views.otp_verify.OtpVerifyActivity
import kotlinx.coroutines.launch


class RegisterViewModel(
    private val usersRepository: UserRepository,
) : ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading





    fun navigateToOtpVerify(mobile: String,activity: RegisterActivity) {
        val homeIntent = Intent(activity, OtpVerifyActivity::class.java)
        homeIntent.putExtra("mobile", mobile);
        activity.startActivity(homeIntent)
    }



    fun registerUser(mobile: String,name:String, activity: RegisterActivity) {
        _isLoading.value = true
        viewModelScope.launch {
            usersRepository.registerUser(UserApiRequestDTO(mobile = mobile, name = name))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            if (it.data?.status == "success") {
                                Log.d("TAG_X", it.data.toString())
                                activity.runOnUiThread {
                                    val msg = it.data.message
                                    Toast.makeText(
                                        activity,
                                        msg,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                                navigateToOtpVerify(mobile, activity)
                            } else {
                                activity.runOnUiThread {
                                    Toast.makeText(
                                        activity,
                                        it.data?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                //_errorState.value = ErrorState.NoData
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoading.value = true
                        }

                        Result.Status.ERROR -> {
                            activity.runOnUiThread {
                                val errorMsg =
                                    if (it.error != null) it.error.message else activity.resources.getString(
                                        R.string.server_error_desc
                                    )
                                Toast.makeText(
                                    activity,
                                    errorMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                            _isLoading.value = false
                        }

                        else -> {
                            // _errorState.value = ErrorState.NoData
                        }
                    }
                }
        }
    }
}