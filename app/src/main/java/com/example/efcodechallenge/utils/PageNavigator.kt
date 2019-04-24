package com.example.efcodechallenge.utils

import android.content.Intent
import android.support.v4.app.Fragment

class PageNavigator {
    companion object Dispatcher {
        /**
         * Fires an intent to spin up the "file chooser" UI and select an image.
         */
        fun startActivityForFileSearch(fragment: Fragment, requestCode: Int) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                type = "image/*"
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }
}