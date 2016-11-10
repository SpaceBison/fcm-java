package org.spacebison.fcmjava;

import com.google.firebase.fcm.FcmDownstreamMessage;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FirebaseCloudMessagingApi {
    @POST("/send")
    Response<String> send(@Header("Authorization") String authorization, @Body FcmDownstreamMessage message);
}
