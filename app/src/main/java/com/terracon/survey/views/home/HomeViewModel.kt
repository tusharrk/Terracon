package com.terracon.survey.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.terracon.survey.data.ProjectRepository
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.utils.AppUtils
import kotlinx.coroutines.launch


class HomeViewModel(
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    private val _projectsList = MutableLiveData<List<Project>?>()
    val projectsList: MutableLiveData<List<Project>?> = _projectsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<ErrorState?>()
    val errorState: LiveData<ErrorState?> = _errorState


    private var gson = GsonBuilder().setLenient().serializeNulls().create()

    //val user: User? = AppUtils.getUserData()

    init {
        fetchProjects("asd")
    }

    fun fetchProjects(userId: String) {
        _isLoading.value = true
        _errorState.value = null
        viewModelScope.launch {
            projectRepository.getAllProjects(UserApiRequestDTO(id = 0))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            _errorState.value = null
                            if (it.data?.isNotEmpty() == true) {
                                _projectsList.value =
                                    it.data.sortedBy { projects -> projects.projectName }
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
