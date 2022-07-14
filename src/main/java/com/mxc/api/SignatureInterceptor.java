package com.mxc.api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;

import org.jetbrains.annotations.NotNull;

import com.mxc.api.basic.SignatureUtil;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


@Slf4j
public class SignatureInterceptor implements Interceptor {

    private static final String HEADER_ACCESS_KEY = "X-MEXC-APIKEY";
    private final String secretKey;
    private final String accessKey;

    public SignatureInterceptor(String secretKey, String accessKey) {
        this.secretKey = secretKey;
        this.accessKey = accessKey;
    }


    @NotNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request origRequest = chain.request();
        String method = origRequest.method();
        Request newRequest;
        if ("GET".equals(method)) {
            newRequest = createUrlSignRequest(origRequest);
        } else if ("POST".equals(method) || "DELETE".equals(method)) {
            RequestBody origBody = origRequest.body();
            if (origBody != null) {
                //support body params
                newRequest = createBodySignRequest(origRequest, origBody, method);
            } else {
                //support query params
                newRequest = createUrlSignRequest(origRequest);
            }
        } else {
            return chain.proceed(origRequest);
        }
        return chain.proceed(newRequest);
    }

    private Request createBodySignRequest(Request origRequest, RequestBody origBody, String method) {
        String timestamp = Instant.now().toEpochMilli() + "";
        String params = bodyToString(origBody);
        params += "&timestamp=" + timestamp;
        String signature = SignatureUtil.actualSignature(params, secretKey);
        params += "&signature=" + signature;
        if ("POST".equals(method)) {
            return origRequest.newBuilder()
                    .addHeader(HEADER_ACCESS_KEY, accessKey)
                    .post(RequestBody.create(params, MediaType.get("text/plain"))).build();
        } else {
            return origRequest.newBuilder()
                    .addHeader(HEADER_ACCESS_KEY, accessKey)
                    .delete(RequestBody.create(params, MediaType.get("text/plain"))).build();
        }
    }

    private Request createUrlSignRequest(Request request) {
        String timestamp = Instant.now().toEpochMilli() + "";
        HttpUrl url = request.url();
        HttpUrl.Builder urlBuilder = url
                .newBuilder()
                .setQueryParameter("timestamp", timestamp);
        String queryParams = urlBuilder.build().query();
        urlBuilder.setQueryParameter("signature", SignatureUtil.actualSignature(queryParams, secretKey));
        return request.newBuilder()
                .addHeader(HEADER_ACCESS_KEY, accessKey)
                .url(urlBuilder.build()).build();
    }


    private String bodyToString(RequestBody body) {
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readString(Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}


