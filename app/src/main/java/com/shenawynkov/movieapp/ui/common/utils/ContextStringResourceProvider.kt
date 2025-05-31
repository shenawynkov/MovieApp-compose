package com.shenawynkov.movieapp.ui.common.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextStringResourceProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : StringResourceProvider {
        override fun getString(stringResId: Int): String = context.getString(stringResId)

        override fun getString(
            stringResId: Int,
            vararg formatArgs: Any,
        ): String = context.getString(stringResId, *formatArgs)
    } 
