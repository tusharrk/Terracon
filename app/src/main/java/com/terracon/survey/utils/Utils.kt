package com.terracon.survey.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.terracon.survey.R
import com.terracon.survey.model.User
import com.terracon.survey.views.login.LoginActivity

object AppUtils {
    private var gson = GsonBuilder().setLenient().serializeNulls().create()

    fun getUserData(): User? {
        return try {
            gson.fromJson(
                Prefs["userData", ""],
                User::class.java
            )
        }catch (e:Exception){
            null
        }

    }

    fun spannableStringWithColor(view: TextView, start:Int, end:Int, s: String, color:Int, isUnderLine: Boolean = true) {
//        val wordtoSpan: Spannable =
//            SpannableString(s)
//        wordtoSpan.setSpan(
//            ForegroundColorSpan(ContextCompat.getColor(view.context, color)),
//            start,
//            end,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        view.text = wordtoSpan

        val text = buildSpannedString {
            append(s)
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(view.context, color)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                start, // start
                end, // end
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if(isUnderLine){
                setSpan(
                    UnderlineSpan(),
                    start, // start
                    end, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

        }
        view.text = text
    }

    // image utils
    fun imageWithGLide(imageView: ImageView, url: String?) {
        when (url) {
            null -> Unit
            "" -> imageView.setBackgroundResource(R.drawable.baseline_image_24)
            else -> Glide.with(imageView.context).load(url).error(R.drawable.ic_baseline_error_24)
                .into(imageView)
        }
    }

    fun logoutUser(activity: Activity){
        Toast.makeText(activity, "Logout", Toast.LENGTH_LONG).show()
        val intent = Intent(activity, LoginActivity::class.java)
        activity.startActivity(intent)
        activity.finishAffinity()
    }
}