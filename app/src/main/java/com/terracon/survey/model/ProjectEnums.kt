package com.terracon.survey.model


enum class ProjectTypeEnums(private val symbol: String) {
    TREE_ASSESSMENT("tree_assessment"),
    BIO_DIVERSITY("bio_diversity");

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



