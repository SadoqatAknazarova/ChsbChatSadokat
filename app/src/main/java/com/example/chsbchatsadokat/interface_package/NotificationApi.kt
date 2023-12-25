package com.example.chsbchatsadokat.firebasechat.`interface`
import com.example.chsbchatsadokat.firebasechat.model.PushNotification
import com.example.chsbchatsadokat.Constans.Constants.Companion.CONTENT_TYPE
import com.example.chsbchatsadokat.Constans.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: key=$SERVER_KEY","Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification:PushNotification
    ): Response<ResponseBody>
}