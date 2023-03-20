package com.example.camerademo

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.camerademo.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainComposeView.setContent {
            
        }
        askPermission(this)


    }
}

fun askPermission(activity: FragmentActivity) {
    val requestList = mutableListOf<String>()
    if (ContextCompat.checkSelfPermission(
        activity,
        android.Manifest.permission.CAMERA
    ) != PackageManager.PERMISSION_GRANTED) {
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
