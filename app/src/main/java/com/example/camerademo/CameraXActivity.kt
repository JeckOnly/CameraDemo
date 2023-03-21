package com.example.camerademo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.camerademo.camera2.Camera2Activity
import com.example.camerademo.cameraX.startCamera
import com.example.camerademo.databinding.ActivityCameraxBinding
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camerax)
        binding.cameraxComposeView.setContent {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Button(onClick = {
                        startCamera(this@MainActivity, binding.cameraxPreview)
                    }) {
                        Text(text = "cameraX预览")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = {
                        startActivity(Intent(this@MainActivity, Camera2Activity::class.java))
                    }) {
                        Text(text = "跳转到camera2")
                    }
                }
            }
        }
        askPermission(this)

    }
}

fun askPermission(activity: FragmentActivity) {
    val requestList = mutableListOf<String>()
    if (ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestList.add(android.Manifest.permission.CAMERA)
    }
    if (requestList.isNotEmpty()) {
        PermissionX.init(activity)
            .permissions(requestList)
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                val message = "The Application needs permissions below to run"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(activity, "All permisssions are allowed", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(activity, "You have denied：$deniedList", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}
