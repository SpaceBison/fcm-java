package org.spacebison.fcmjava;

import com.google.firebase.fcm.FcmDownstreamMessage;
import com.google.firebase.fcm.FcmDownstreamMessageResult;
import com.google.firebase.fcm.FcmDownstreamTopicMessageResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

interface FirebaseCloudMessagingService {
    @POST("send")
    Call<FcmDownstreamMessageResult> send(@Header("Authorization") String authorization, @Body FcmDownstreamMessage message);

    @POST("send")
    Call<FcmDownstreamTopicMessageResult> sendTopic(@Header("Authorization") String authorization, @Body FcmDownstreamMessage message);
}
