package com.example.efcodechallenge.viewmodel

import com.example.efcodechallenge.repository.`interface`.FileRepository
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * This class acts like a hub among all fragments and our activity. In this app we are using a single activity with multiple fragments.
 * Communication among the fragments and activity is accomplished using a common viewModel and liveData objects. We are using a publish-subscribe model to eliminate calling dead objects.
 * Common problem is that object that are part of a callback maybe gone by the time the callback is invoked. By pub-sub method we can guarantee that subscribed object will be there to pull the data.
 * We have a live data for different events.
 *
 * Actual network interaction takes place in FileRepository, which is injected into our viewModel by Koin dependency injector.
 */
class UploadedImageViewModel(val fileRepository: FileRepository) : ViewModel() {
    // Live events.
    // recentlyUploaded will be fired when file upload is finished.
    var recentlyUploaded: MutableLiveData<String> = MutableLiveData()
    // uploadResult will be fired when upload result changes. True means successful upload and false indicates failure.
    var uploadResult: MutableLiveData<Boolean> = MutableLiveData()
    // uploadProgress will indicate the percentage upload.
    var uploadProgress: MutableLiveData<Int> = MutableLiveData()

    var readyToUpload: Boolean = false
    lateinit var uploadPath: String

    /**
     * Use this method to start uploading the file.
     */
    fun upload(path: String) {
        val checkForSuccess: (success: Boolean)->Unit = fun(success) {
            if (success) {
                // using postValue for thread safety
                recentlyUploaded.postValue(path)
            }
            uploadResult.postValue(success)
        }
        val updateProgressBar: (progress: Long)->Unit = fun(progress) {
            // using postValue for thread safety
            uploadProgress.postValue(progress.toInt())
        }
        fileRepository.upload(path, checkForSuccess, updateProgressBar)
    }
}