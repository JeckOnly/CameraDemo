package com.example.camerademo.camera2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import com.example.camerademo.R
import com.example.camerademo.camera1.Camera1Activity
import com.example.camerademo.databinding.ActivityCamera2Binding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class Camera2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCamera2Binding
    private lateinit var cameraManager: CameraManager
    private lateinit var imageReader: ImageReader
    private lateinit var cameraId: String
    private var cameraDevice: CameraDevice? = null

    // TODO 创建一个Handler和一个后台线程绑定
    private val backgroundHandler: Handler by lazy {
        val looper = HandlerThread("imageReader").looper
        Handler(looper)
    }

    private val cameraDeviceStateCallback: CameraDevice.StateCallback by lazy {
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Timber.d("onOpened")
                cameraDevice = camera
                createCameraPreviewSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                Timber.d("onDisconnected")
                cameraDevice = null
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Timber.d("onError")
                cameraDevice = null
            }

        }
    }

    private val captureSessionCaptureCallback: CameraCaptureSession.CaptureCallback by lazy {
        object : CaptureCallback() {
            override fun onCaptureStarted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                timestamp: Long,
                frameNumber: Long
            ) {
                Timber.d("capture started")
            }

            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                Timber.d("capture completed")
            }

            override fun onCaptureProgressed(
                session: CameraCaptureSession,
                request: CaptureRequest,
                partialResult: CaptureResult
            ) {
                Timber.d("capture progress")
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera2)
        binding.camera2ComposeView.setContent {
            MyContent()
        }
        initCamera()
    }

    @Composable
    private fun MyContent() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Row(Modifier.align(Alignment.Center)) {
                Button(onClick = {
                    turnOnCameraPreview()
                }) {
                    Text(text = "camera2预览")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(onClick = {
                    startActivity(Intent(this@Camera2Activity, Camera1Activity::class.java))
                }) {
                    Text(text = "前往camera1预览")
                }
            }
        }
    }

    /**
     * 初始化相机的一些设置
     */
    fun initCamera() {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = getCameraId(cameraManager)
    }


    /**
     * 获取相机信息
     *
     * @param front 是否选择前置相机
     *
     */
    fun getCameraId(cameraManager: CameraManager, front: Boolean = false): String {
        val cameraIds: Array<String> = cameraManager.cameraIdList
        var cameraId: String = ""
        val targetCamera = if (front) CameraCharacteristics.LENS_FACING_FRONT else CameraCharacteristics.LENS_FACING_BACK
        for (id in cameraIds) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == targetCamera) {
                initImageReader(cameraCharacteristics)
                cameraId = id
                break
            }
        }
        Timber.d("cameraId: $cameraId")
        return cameraId
    }

    fun initImageReader(cameraCharacteristics: CameraCharacteristics) {
        val previewSize =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                .getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!
        imageReader =
            ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 2)
        imageReader.setOnImageAvailableListener(ImageReader.OnImageAvailableListener { reader ->
            Timber.d("onImageAvailable: ${reader.acquireNextImage()}")
        }, null)
    }

    @SuppressLint("MissingPermission")
    fun turnOnCameraPreview() {

        cameraManager.openCamera(
            cameraId, cameraDeviceStateCallback, null
        )
    }

    private fun createCameraPreviewSession() {
        val tempCameraDevice = cameraDevice
        tempCameraDevice?.let {
            val texture: SurfaceTexture = binding.camera2Preview.surfaceTexture!!
            texture.setDefaultBufferSize(
                binding.camera2Preview.width,
                binding.camera2Preview.height
            )
            val surface = Surface(texture)
            try {
                val previewRequestBuilder =
                    tempCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                previewRequestBuilder.addTarget(surface)

                // Here, we create a CameraCaptureSession for camera preview.
                tempCameraDevice.createCaptureSession(
                    listOf(surface, imageReader.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            // The camera is already closed
                            cameraDevice ?: return
                            // When the session is ready, we start displaying the preview.
                            try {
                                // Auto focus should be continuous for camera preview.
                                previewRequestBuilder.set(
                                    CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                                )

                                // Finally, we start displaying the camera preview.
                                val previewRequest = previewRequestBuilder.build() ?: return
                                cameraCaptureSession.setRepeatingRequest(
                                    previewRequest, captureSessionCaptureCallback, null
                                )
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onConfigureFailed(
                            cameraCaptureSession: CameraCaptureSession
                        ) {
                            Toast.makeText(
                                this@Camera2Activity,
                                "configureFailed",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                    null
                )
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }
}

