package com.terracon.survey.views.flora_fauna

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.michaelflisar.lumberjack.L
import com.terracon.survey.data.ProjectRepository
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.FloraFaunaCategoryResponse
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.utils.Prefs
import com.terracon.survey.views.login.LoginActivity
import com.terracon.survey.views.otp_verify.OtpVerifyActivity
import kotlinx.coroutines.launch


class FloraFaunaViewModel(
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    private val _floraFaunaList = MutableLiveData<FloraFaunaCategoryResponse?>()
    val floraFaunaList: MutableLiveData<FloraFaunaCategoryResponse?> = _floraFaunaList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<ErrorState?>()
    val errorState: LiveData<ErrorState?> = _errorState
    lateinit var  project: Project


    private var gson = GsonBuilder().setLenient().serializeNulls().create()
    init {
        fetchFloraFaunaCategories("asd")
    }
     fun fetchFloraFaunaCategories(userId: String) {
        _isLoading.value = true
        _errorState.value = null
        viewModelScope.launch {
            projectRepository.getFloraFaunaCategories(UserApiRequestDTO(id = 0))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            _errorState.value = null
                            if (it.data != null) {
                                _floraFaunaList.value = it.data
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