package com.blackcat.android.data.remote.service

import com.blackcat.android.data.remote.parser.SagawaHtmlParser
import com.blackcat.android.domain.model.TrackingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class SagawaTrackingService @Inject constructor(
    private val client: OkHttpClient
) {
    companion object {
        private const val BASE_URL = "https://k2k.sagawa-exp.co.jp/p/web/okurijosearch.do"
    }

    suspend fun track(trackingNumber: String): TrackingResult = withContext(Dispatchers.IO) {
        try {
            val formBody = FormBody.Builder()
                .add("okurijoNo", trackingNumber)
                .build()

            val request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext TrackingResult.Error("HTTP ${response.code}")
            }

            val html = response.body?.string() ?: return@withContext TrackingResult.Error("空のレスポンス")
            val statusList = SagawaHtmlParser.parse(html)

            if (statusList.isEmpty()) {
                TrackingResult.Error("追跡情報が見つかりません")
            } else {
                TrackingResult.Success(statusList)
            }
        } catch (e: Exception) {
            TrackingResult.Error(e.message ?: "不明なエラー")
        }
    }
}
