package com.terracon.survey.views.splash

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.michaelflisar.lumberjack.L
import com.terracon.survey.R
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.Result
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.model.UserResponse
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.GlobalData
import com.terracon.survey.utils.Prefs
import com.terracon.survey.views.home.HomeActivity
import com.terracon.survey.views.login.LoginActivity
import com.terracon.survey.views.otp_verify.OtpVerifyActivity
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule


class SplashViewModel(
    private val usersRepository: UserRepository,
) : ViewModel() {

    private val _usersList = MutableLiveData<List<User>?>()
    val usersList: MutableLiveData<List<User>?> = _usersList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<ErrorState?>()
    val errorState: LiveData<ErrorState?> = _errorState


    private var gson = GsonBuilder().setLenient().serializeNulls().create()


    fun checkIfUserLoggedIn(activity: SplashActivity) {
        val userId: Int = Prefs["userId", 0]
        if (userId != 0) {
            L.d { "user is already logged in: $userId" }
            val user: User? = AppUtils.getUserData()
            if (user != null) {
                GlobalData.userData = user
                getUserDetails(userId, activity)
            } else {
                navigateToLogin(activity = activity)
            }
        } else {
            L.d { "user is not logged in..." }
            navigateToLogin(activity = activity)
        }
    }

    private fun navigateToLogin(activity: SplashActivity) {
        Timer().schedule(1000) {
            val homeIntent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(homeIntent)
            activity.finish()
        }
    }

    private fun navigateToHome(activity: SplashActivity) {
        Timer().schedule(3000) {
            val homeIntent = Intent(activity, HomeActivity::class.java)
            activity.startActivity(homeIntent)
            activity.finish()
        }
    }

    private fun getUserDetails(user: User, activity: OtpVerifyActivity) {
        _isLoading.value = true
        viewModelScope.launch {
            usersRepository.loginUser(UserApiRequestDTO(mobile = mobile, otp = otp))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            if (it.data?.status == "success") {
                                Log.d("TAG_X", it.data.toString())
                                saveSuccessUserLoginData(it.data,activity)
                                activity.runOnUiThread {
                                    val msg = it.data.message
                                    Toast.makeText(
                                        activity,
                                        msg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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

    private fun saveSuccessUserLoginData(userResponse: UserResponse, activity: SplashActivity){
        Prefs["userId"] = userResponse.data.user.id
        Prefs["token"] = userResponse.data.user.token
        Prefs["userData"] = Gson().toJson(userResponse.data.user)
        GlobalData.userData = userResponse.data.user

        navigateToHome(activity)
    }
}