/*
 * Copyright 2022 Adler O. S. Neves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.punchthrough.blestarterappandroid

import android.content.Context
import android.widget.Toast
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit


class HeartRateUploadTarget(private val ctx: Context, private val endpoint: String) {
    fun send(reading: Short){
        Timber.i("HTTP POST to $endpoint with $reading")
        doAsync {
            try{
                val url = URL(endpoint)
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder()
                    .url(url)
                    .post(FormBody.Builder().addEncoded("rate", "$reading").build())
                    .build()
                val response = client.newCall(request).execute()
                if(response.code() != 200) throw Exception("Server replied ${response.code()}")
                response.close()
            } catch (e: Exception) {
                e.printStackTrace()
                uiThread {
                    Toast.makeText(it.ctx, "Network failure: $e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
