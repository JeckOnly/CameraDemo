package com.example.camerademo.util

import timber.log.Timber

class FileClassMethodTag: Timber.DebugTree(){
    override fun createStackElementTag(e: StackTraceElement): String {
        return "[${e.fileName}][${e.methodName}]"
    }
}