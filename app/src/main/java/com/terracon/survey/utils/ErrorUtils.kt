package com.terracon.survey.utils

import android.content.Context
import com.terracon.survey.R
import com.terracon.survey.databinding.ErrorViewBinding
import com.terracon.survey.model.Error
import com.terracon.survey.model.ErrorState
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * parses error response body
 */
object ErrorUtils {

    fun parseError(response: Response<*>, retrofit: Retrofit): Error? {
        val converter = retrofit.responseBodyConverter<Error>(Error::class.java, arrayOfNulls(0))
        return try {
            converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            Error()
        }
    }

    fun setErrorView(
        errorMessage: ErrorState,
        errorBinding: ErrorViewBinding,
        context: Context,
        pageName: String,
        onRetry: () -> Unit
    ) {
        when (errorMessage) {
            ErrorState.NoData -> {
                errorBinding.errorImageView.setImageResource(R.drawable.baseline_inbox_24)
                errorBinding.errorTitle.text = when (pageName) {
                    "projects" -> context.getString(
                        R.string.no_projects_error_title
                    )
//                    "user" -> context.getString(
//                        R.string.no_user_error_title
//                    )
//                    "message" -> context.getString(
//                        R.string.no_message_error_title
//                    )
                    else -> context.getString(
                        R.string.no_data_error_title
                    )
                }
                errorBinding.errorDesc.text = when (pageName) {
                    "projects" -> context.getString(
                        R.string.no_projects_error_desc
                    )
//                    "user" -> context.getString(
//                        R.string.no_user_error_desc
//                    )
//                    "message" -> context.getString(
//                        R.string.no_message_error_desc
//                    )
                    else -> context.getString(
                        R.string.no_data_error_desc
                    )
                }
            }

            ErrorState.NetworkError -> {
                errorBinding.errorImageView.setImageResource(R.drawable.baseline_electrical_services_24)
                errorBinding.errorTitle.text = context.getString(
                    R.string.internet_error_title
                )
                errorBinding.errorDesc.text = context.getString(
                    R.string.internet_error_desc
                )
            }

            ErrorState.ServerError -> {
                errorBinding.errorImageView.setImageResource(R.drawable.baseline_cloud_off_24)
                errorBinding.errorTitle.text = context.getString(
                    R.string.server_error_title
                )
                errorBinding.errorDesc.text = context.getString(
                    R.string.server_error_desc
                )
            }

        }
        errorBinding.retryBtn.setOnClickListener {
            onRetry()
        }
    }
}