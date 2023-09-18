package com.terracon.survey.model

data class Error(val status_code: Int = 0,
                 val status_message: String? = null)


sealed class ErrorState {
    object NetworkError : ErrorState()
    object ServerError : ErrorState()
    object NoData : ErrorState()
}