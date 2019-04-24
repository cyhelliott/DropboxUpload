package com.example.efcodechallenge.koin

import com.example.efcodechallenge.repository.`interface`.FileRepository
import com.example.efcodechallenge.repository.implementation.DropboxImageRepository
import com.example.efcodechallenge.viewmodel.UploadedImageViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

// For Koin injector
val appModule = module {

    // single instance of FileRepository
    single<FileRepository> { DropboxImageRepository() }

    // UploadedImageViewModel
    viewModel { UploadedImageViewModel(get()) }
}