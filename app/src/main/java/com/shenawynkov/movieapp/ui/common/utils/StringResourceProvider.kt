package com.shenawynkov.movieapp.ui.common.utils

interface StringResourceProvider {
    fun getString(stringResId: Int): String

    fun getString(
        stringResId: Int,
        vararg formatArgs: Any,
    ): String
} 
