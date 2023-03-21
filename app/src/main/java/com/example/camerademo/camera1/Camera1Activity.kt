package com.example.camerademo.camera1

import android.graphics.ImageFormat
import android.hardware.Camera
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import com.example.camerademo.R
import com.example.camerademo.databinding.ActivityCamera1Binding
import com.example.camerademo.databinding.ActivityCameraxBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Camera1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCamera1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera1)
        binding.camera1ComposeView.setContent {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Button(onClick = {
                        startPreview()
                    }) {
                        Text(text = "camera1预览")
                    }
                }
            }
        }
    }

    fun startPreview() {
        val camera: Camera =
            Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK) // CAMERA_ID通常为Camera.CameraInfo.CAMERA_FACING_BACK或CAMERA_FACING_FRONT

        val surfaceView = binding.camera1Preview
        camera.parameters.apply {
            setPreviewSize(surfaceView.width, surfaceView.height)
            // 设置图片格式
            setPictureFormat(ImageFormat.JPEG)
            // 设置图片质量
            setJpegQuality(90)
            // 闪光灯设置，调用之前最好动过Camera.Parameters#getSupportedFlashModes来判断一下当前设备是否支持闪光灯
            setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
        }

        // 设置目标
        camera.apply {
            setPreviewDisplay(surfaceView.holder)
            setDisplayOrientation(90)
            // 开始预览
            startPreview()
        }
    }
}