package com.terracon.survey.model


enum class TopicEnums(private val symbol: String) {
    TOPIC_STATUS("status.messages"),
    TOPIC_CHAT_MESSAGE("private.messages");

    override fun toString(): String {
        return symbol
    }
}

enum class TypeEnums(private val symbol: String) {
    TYPE_PUBLIC("public"),
    TYPE_PRIVATE("private");

    override fun toString(): String {
        return symbol
    }
}



