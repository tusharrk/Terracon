package com.terracon.survey.views.register

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
import com.terracon.survey.views.login.LoginActivity
import com.terracon.survey.views.otp_verify.OtpVerifyActivity
import kotlinx.coroutines.launch


class RegisterViewModel(
    private val usersRepository: UserRepository,
) : ViewModel() {

    private val _usersList = MutableLiveData<List<User>?>()
    val usersList: MutableLiveData<List<User>?> = _usersList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<ErrorState?>()
    val errorState: LiveData<ErrorState?> = _errorState



    private var gson = GsonBuilder().setLenient().serializeNulls().create()

    fun navigateToOtpVerify(activity: RegisterActivity) {
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

    private fun fetchUsersDetailsFromServer(userId: String,activity: LoginActivity) {
        _isLoading.value = true
        _errorState.value = null
        viewModelScope.launch {
            usersRepository.getAllUsers(UserApiRequestDTO(id = 0))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            _errorState.value = null
                            if (it.data?.isNotEmpty() == true) {
                                Log.d("TAG_X",it.data.toString())
                            } else {
                                _errorState.value = ErrorState.NoData
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoading.value = true
                            _errorState.value = null
                        }

                        Result.Status.ERROR -> {
                            _isLoading.value = false
                            _errorState.value = ErrorState.ServerError
                        }

                        else -> _errorState.value = ErrorState.NoData
                    }
                }


        }
    }
}