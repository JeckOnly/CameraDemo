package com.example.camerademo

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraXViewModel @Inject constructor(
    val app: Application
): ViewModel() {
}