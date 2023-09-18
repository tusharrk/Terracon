package com.terracon.survey.common


open class Event<out T>(private val content: T) {
    private var isHandled = false

    fun getContentIfNotHandled(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            content
        }
    }
}

//abstract class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
//    override fun onChanged(value: Event<T>) {
//        event?.getContentIfNotHandled()?.let { onEventUnhandledContent(it) }
//    }
//}
