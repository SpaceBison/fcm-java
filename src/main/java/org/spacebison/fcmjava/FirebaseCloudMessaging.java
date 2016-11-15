package org.spacebison.fcmjava;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cmb on 11.11.16.
 */

public class FirebaseCloudMessaging {
    public static final int ERROR_NETWORK_ERROR = -1;
    public static final int ERROR_INVALID_RESPONSE = -2;
    public static final int ERROR_NO_API_KEY = -3;
    private final Gson mGson = new Gson();
    private final FirebaseCloudMessagingService mService = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .baseUrl("https://fcm.googleapis.com/fcm/")
            .build()
            .create(FirebaseCloudMessagingService.class);
    private String mApiKey;

    private FirebaseCloudMessaging() {}

    public static FirebaseCloudMessaging getInstance() {
        return Holder.INSTANCE;
    }

    public void setApiKey(String apiKey) {
        mApiKey = apiKey;
    }

    public void sendMessage(FcmDownstreamMessage message, Listener<FcmDownstreamMessageResult> listener) {
        if (mApiKey == null) {
            listener.onError(ERROR_NO_API_KEY, "Missing api key");
            return;
        }

        mService.send(getApiKeyHeader(mApiKey), message).enqueue(new BaseCallback<>(listener));
    }

    public void sendTopicMessage(FcmDownstreamMessage message, Listener<FcmDownstreamTopicMessageResult> listener) {
        if (mApiKey == null) {
            listener.onError(ERROR_NO_API_KEY, "Missing api key");
            return;
        }

        mService.sendTopic(getApiKeyHeader(mApiKey), message).enqueue(new BaseCallback<>(listener));
    }

    private static String getApiKeyHeader(String apiKey) {
        return "key=" + apiKey;
    }

    public interface Listener<T> {
        void onCompleted(T result);
        void onError(int code, String message);
    }

    private class BaseCallback<T> implements Callback<T> {
        private final Listener<T> mListener;

        private BaseCallback(Listener<T> listener) {
            mListener = listener;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (mListener == null) {
                return;
            }

            if (response.isSuccessful()) {
                mListener.onCompleted(response.body());
            } else {
                try {
                    String errorBody = response.errorBody().string();
                    try {
                        T result = mGson.fromJson(errorBody, new TypeToken<T>() {}.getType());
                        mListener.onCompleted(result);
                    } catch (JsonSyntaxException e) {
                        System.out.println(errorBody);
                        mListener.onError(ERROR_INVALID_RESPONSE, errorBody);
                    }
                } catch (IOException e) {
                    mListener.onError(ERROR_INVALID_RESPONSE, null);
                }
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if (mListener != null) {
                mListener.onError(ERROR_NETWORK_ERROR, t.getLocalizedMessage());
            }
        }
    }

    private static class Holder {
        private static final FirebaseCloudMessaging INSTANCE = new FirebaseCloudMessaging();
    }
}
