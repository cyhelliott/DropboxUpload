package com.example.efcodechallenge.repository.`interface`

interface FileRepository {
    fun upload(path: String, checkForSuccess: (Boolean) -> Unit, updateProgressBar: (Long) -> Unit)
    fun setAccountDetails()
}