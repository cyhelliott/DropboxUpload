package com.example.efcodechallenge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.example.efcodechallenge.fragment.UploadImageFragment


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION: Int = 43

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                var uploadImageFragment = UploadImageFragment()
                supportFragmentManager.beginTransaction()
                    .attach(uploadImageFragment)
                    .add(R.id.fl_main, uploadImageFragment)
                    .commit()
            }
        }
    }

    /*
     * For requesting external storage read permission
     */
    private fun requestPermission(permissions: Array<String>) {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, permissions,
                REQUEST_PERMISSION
            )
        } else {
            var uploadImageFragment = UploadImageFragment()
            supportFragmentManager.beginTransaction()
                .attach(uploadImageFragment)
                .add(R.id.fl_main, uploadImageFragment)
                .commit()
        }

    }

}
