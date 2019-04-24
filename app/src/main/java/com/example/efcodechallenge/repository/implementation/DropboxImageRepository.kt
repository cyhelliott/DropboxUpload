package com.example.efcodechallenge.repository.implementation

import com.example.efcodechallenge.repository.`interface`.FileRepository
import android.support.annotation.WorkerThread
import android.util.Log
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileInputStream
import kotlin.math.roundToLong

class DropboxImageRepository : FileRepository {
    // Access Token for user's dropbox account, can be generated on Dropbox website, https://www.dropbox.com/developers/apps
    private val ACCESS_TOKEN = "W6ZaHeDbULAAAAAAAAAAC5SRq0Tf4kJroAgk2iLwQ8Ng8k8vX7hBFPRuPH-MbfBQ"
    private val PERCENT_CONVERSION = 100

    lateinit var client: DbxClientV2
    lateinit var account: FullAccount

    /**
     * Uploads file to dropbox, informs com.example.efcodechallenge.com.example.efcodechallenge.fragment of progress and success or failure
     */
    override fun upload(path: String, checkForSuccess: (Boolean) -> Unit, updateProgressBar: (Long) -> Unit) {
        GlobalScope.launch {
            FileInputStream(path).use { `in` ->
                try {
                    var fileSize = `in`.channel.size()
                    client.files().uploadBuilder(path)
                        .uploadAndFinish(`in`) { long ->
                            Log.v(
                                "Elliott",
                                (long.toFloat() / fileSize.toFloat() * PERCENT_CONVERSION).roundToLong().toString()
                            )
                            updateProgressBar((long.toFloat() / fileSize.toFloat() * PERCENT_CONVERSION).roundToLong())
                        }
                    checkForSuccess(true)
                } catch (except: DbxException) {
                    checkForSuccess(false)
                }
            }
        }
    }

    /**
     * Launcher method for starting client information initialization
     */
    override fun setAccountDetails() {
        GlobalScope.launch(Dispatchers.Default) {
            makeNetworkRequest()
        }
    }

    @WorkerThread
    suspend fun makeNetworkRequest() {
        setAccount()
    }

    suspend fun setAccount() {
        client = DbxClientV2(DbxRequestConfig.newBuilder("dropbox/java-tutorial").build(), ACCESS_TOKEN)
        account = client.users().currentAccount
    }
}