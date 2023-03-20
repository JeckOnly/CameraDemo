package com.example.camerademo.cameraX

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

 fun startCamera(context: Context, previewView: PreviewView) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

     val runnable = Runnable {
         // Used to bind the lifecycle of cameras to the lifecycle owner
         val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

         // Preview
         val preview = Preview.Builder()
             .build()
             .also {
                 it.setSurfaceProvider(previewView.surfaceProvider)
             }

         // Select back camera as a default
         val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

         try {
             // Unbind use cases before rebinding
             cameraProvider.unbindAll()

             // Bind use cases to camera
             cameraProvider.bindToLifecycle(
                 context as LifecycleOwner, cameraSelector, preview)

         } catch(exc: Exception) {
             Timber.d("bind fail")
         }
     }
    cameraProviderFuture.addListener(runnable, ContextCompat.getMainExecutor(context))
}