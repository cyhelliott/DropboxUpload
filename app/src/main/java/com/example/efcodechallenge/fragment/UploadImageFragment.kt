package com.example.efcodechallenge.fragment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.efcodechallenge.R
import kotlinx.android.synthetic.main.fragment_upload_image.*
import org.koin.android.viewmodel.ext.android.viewModel
import com.example.efcodechallenge.utils.PageNavigator
import com.example.efcodechallenge.viewmodel.UploadedImageViewModel


class UploadImageFragment : Fragment() {

    private val GALLERY_REQUEST_CODE: Int = 42
    private val viewModel: UploadedImageViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_image, container, false)
        viewModel.fileRepository.setAccountDetails()

        // sets listeners
        setListeners(view)

        // sets observers
        observeLiveData()

        return view
    }

    /**
     * Sets listeners
     */
    private fun setListeners(view: View) {
        view.findViewById<Button>(R.id.btn_file).setOnClickListener {
            if (viewModel.readyToUpload) {
                uploadImage()
            } else {
                PageNavigator.startActivityForFileSearch(this, GALLERY_REQUEST_CODE)
            }
        }

        // for resetting file path display
        view.findViewById<ImageView>(R.id.iv_cancel_file_path).setOnClickListener {
            viewModel.readyToUpload = false
            btn_file.text = getString(R.string.select_file)
            tv_file_path.text = getString(R.string.no_file_selected)
        }
    }

    // Set observers
    private fun observeLiveData() {
        viewModel.recentlyUploaded.observe(this, Observer<String> { uri: String? ->
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

            iv_upload_display.visibility = View.GONE
            Glide.with(context!!)
                .load(uri)
                .transition(withCrossFade(factory))
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        iv_upload_display.visibility = View.VISIBLE
                        pb_upload.visibility = View.GONE
                        tv_percent_uploaded.visibility = View.GONE
                        if (tv_recently_uploaded.visibility != View.VISIBLE) tv_recently_uploaded.visibility =
                            View.VISIBLE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(iv_upload_display)
        })

        viewModel.uploadResult.observe(this, Observer<Boolean> { uploadSuccessful: Boolean? ->
            if (uploadSuccessful!!) {
                tv_file_path.text = getString(R.string.no_file_selected)
                btn_file.text = getString(R.string.select_file)
                viewModel.readyToUpload = false
                Toast.makeText(context, getString(R.string.upload_successful), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, getString(R.string.upload_unsuccessful), Toast.LENGTH_LONG).show()
            }
        })

        viewModel.uploadProgress.observe(this, Observer<Int> { progress: Int? ->
            pb_upload.progress = progress!!
            tv_percent_uploaded.text = progress.toString() + " %"
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                handleGallerySuccess(data!!.data)
            }

        } catch (ex: Exception) {
            Log.d(this.tag, ex.toString())
        }

    }

    private fun getImagePath(uri: Uri): String {
        var cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()

        cursor = context?.contentResolver?.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(document_id),
            null
        )
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()

        return path
    }

    /**
     * Handles the case where an image URI is successfully returned from the gallery activity
     */
    private fun handleGallerySuccess(uri: Uri) {
        viewModel.uploadPath = getImagePath(uri)
        viewModel.readyToUpload = true
        btn_file.text = getString(R.string.upload_file)
        tv_file_path.text = viewModel.uploadPath
    }

    /**
     * Uploads image to dropbox
     */
    private fun uploadImage() {
        pb_upload.visibility = View.VISIBLE
        tv_percent_uploaded.visibility = View.VISIBLE
        viewModel.upload(viewModel.uploadPath)
    }
}
