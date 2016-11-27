package org.spacebison.fcmjava;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

class NoCacheRequestHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain
                .request()
                .newBuilder()
                .addHeader("Cache-Control", "no-cache")
                .build());
    }
}
