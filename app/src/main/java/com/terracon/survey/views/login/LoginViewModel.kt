package com.terracon.survey.views.login

import android.content.Intent
import android.util.Log
import android.view.View
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
import com.terracon.survey.utils.GlobalData
import com.terracon.survey.utils.Prefs
import com.terracon.survey.views.home.HomeActivity
import kotlinx.coroutines.launch


class LoginViewModel(
    private val usersRepository: UserRepository,
) : ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private var gson = GsonBuilder().setLenient().serializeNulls().create()

//    private fun navigateToOtpVerify(mobile: String, activity: LoginActivity) {
//        val homeIntent = Intent(activity, LoginActivity::class.java)
//        homeIntent.putExtra("mobile", mobile);
//        activity.startActivity(homeIntent)
//    }

    fun checkIfUserLoggedIn(activity: LoginActivity) {
        val userId: String = Prefs["userId", ""]
        if (userId != "") {
            L.d { "user is already logged in: $userId" }
            //fetchUsersDetailsFromServer(userId,activity)
        } else {
            L.d { "user is not logged in..." }

        }
    }

    fun loginUser(mobile: String,password:String, activity: LoginActivity) {
        _isLoading.value = true
        viewModelScope.launch {
            usersRepository.loginUser(UserApiRequestDTO(mobile = mobile,password=password))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            if (it.data?.status == "success") {
                                Log.d("TAG_X", it.data.toString())
                                saveSuccessUserLoginData(it.data, activity)

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
                                    if (it.error?.message != null) it.error.message else activity.resources.getString(
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

    private fun saveSuccessUserLoginData(userResponse: UserResponse, activity: LoginActivity) {
        if (userResponse.data.user.status == "Block") {
            Toast.makeText(
                activity,
                activity.getString(R.string.account_blocked_msg),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Prefs["userId"] = userResponse.data.user.id
            Prefs["token"] = userResponse.data.user.token
            Prefs["userData"] = Gson().toJson(userResponse.data.user)
            GlobalData.userData = userResponse.data.user
            activity.runOnUiThread {
                val msg = userResponse.message
                Toast.makeText(
                    activity,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
            navigateToHome(activity)
        }
    }
    private fun navigateToHome(activity: LoginActivity) {
        val homeIntent = Intent(activity, HomeActivity::class.java)
        activity.startActivity(homeIntent)
        activity.finishAffinity()
    }

    fun showErrorAlert() {

    }
}