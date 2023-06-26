package com.example.muzic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.muzic.R

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class BeginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin)

        requestPermission()
    }

    private fun requestPermission() {
        val listPms = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES
        )
        if (ContextCompat.checkSelfPermission(this, listPms[0]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, listPms[1]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, listPms[2]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, listPms[3]) == PackageManager.PERMISSION_GRANTED

        ) {
            nextActivity()
        } else {
            ActivityCompat.requestPermissions(this, listPms, 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] != PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Please accept permission to continue", Toast.LENGTH_LONG)
                    .show()
            } else {
                nextActivity()
            }
        }
    }

    private fun nextActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}