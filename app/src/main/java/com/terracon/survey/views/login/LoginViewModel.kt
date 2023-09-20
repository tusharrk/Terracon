package com.terracon.survey.views.login

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.michaelflisar.lumberjack.L
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.Result
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.utils.Prefs
import com.terracon.survey.views.otp_verify.OtpVerifyActivity
import kotlinx.coroutines.launch


class LoginViewModel(
    private val usersRepository: UserRepository,
) : ViewModel() {

    private val _usersList = MutableLiveData<List<User>?>()
    val usersList: MutableLiveData<List<User>?> = _usersList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private var gson = GsonBuilder().setLenient().serializeNulls().create()

    fun navigateToOtpVerify(activity: LoginActivity) {
            val homeIntent = Intent(activity, OtpVerifyActivity::class.java)
            activity.startActivity(homeIntent)
    }

    fun checkIfUserLoggedIn(activity: LoginActivity){
        val userId:String = Prefs["userId", ""]
       if(userId != ""){
           L.d { "user is already logged in: $userId" }
           //fetchUsersDetailsFromServer(userId,activity)
       }else{
           L.d { "user is not logged in..." }

       }
    }

     fun loginUser(activity: LoginActivity) {
        _isLoading.value = true
        viewModelScope.launch {
            usersRepository.getAllUsers(UserApiRequestDTO())
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            if (it.data?.isNotEmpty() == true) {
                                Log.d("TAG_X",it.data.toString())
                            } else {
                                //_errorState.value = ErrorState.NoData
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoading.value = true
                        }

                        Result.Status.ERROR -> {
                            _isLoading.value = false
                        }

                        else -> {
                           // _errorState.value = ErrorState.NoData
                        }
                    }
                }


        }
    }

    fun showErrorAlert(){

    }
}