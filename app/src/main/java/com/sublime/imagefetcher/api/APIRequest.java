package com.sublime.imagefetcher.api;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import com.sublime.imagefetcher.BuildConfig;
import com.sublime.imagefetcher.app.ImageFetcher;
import com.sublime.imagefetcher.utils.AppConstants;
import com.sublime.imagefetcher.utils.AppUtils;
import com.sublime.imagefetcher.utils.Timber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by goonerDroid on 03-06-2016.
 */
public class APIRequest {
    private static APIRequest instance;
    private APIService apiService;
    private static final String CACHE_CONTROL = "Cache-Control";


    public static synchronized APIRequest init() {
        if (null == instance) {
            instance = new APIRequest();
            instance.initAPIService();
        }
        return instance;
    }

    @SuppressLint("BinaryOperationInTimber")
    private void initAPIService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(30, TimeUnit.SECONDS);
        b.writeTimeout(30, TimeUnit.SECONDS);
        b.addInterceptor(loggingInterceptor);
        b.addInterceptor(provideOfflineCacheInterceptor());
        b.addNetworkInterceptor(provideCacheInterceptor());
        b.cache(provideCache());
        b.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                request = request.newBuilder()
                        .header("Content-Type", "application/json")
                        .build();
                Timber.i("Request URL >> " + request.url());
                try {
                    return chain.proceed(request);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                }
                return chain.proceed(request);
            }
        });


        OkHttpClient client = b.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addCallAdapterFactory(new ErrorHandlingCallAdapterFactory())
                .client(client)
                .build();
        apiService = retrofit.create(APIService.class);
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(ImageFetcher.getAppInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Timber.e(e, "Could not create Cache!");
        }
        return cache;
    }

    private static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                request.cacheControl().noCache();

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(0, TimeUnit.SECONDS)
                        .noCache()
                        .build();
                if (chain.request().method().equals("GET")) {
                    if (new AppUtils(ImageFetcher.getAppInstance()).isInternetConnected()) {
                        cacheControl = new CacheControl.Builder()
                                .maxAge(0, TimeUnit.SECONDS)
                                .noCache()
                                .build();
                    } else {
                        cacheControl = new CacheControl.Builder()
                                .maxAge(5, TimeUnit.MINUTES)
                                .onlyIfCached()
                                .build();
                    }
                }
                Response response = chain.proceed(chain.request());
                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    private static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!new AppUtils(ImageFetcher.getAppInstance()).isInternetConnected()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }




    private void enqueueCall(RetrofitCall<?> retrofitCall,
                             final OnRequestComplete onRequestComplete) {
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        retrofitCall.enqueue(new RetrofitCallback<Object>() {
            @Override
            public void success(final retrofit2.Response<Object> response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onRequestComplete.onSuccess(response.body());
                    }
                });
            }

            @Override
            public void unauthenticated(final retrofit2.Response<?> response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            onRequestComplete.onAPIFailure(new APIError(jObjError.getString("msg")));
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            onRequestComplete.onAPIFailure(new APIError(AppConstants.SERVER_ERROR_MSG));
                        }
                    }
                });
            }

            @Override
            public void clientError(final retrofit2.Response<?> response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            onRequestComplete.onAPIFailure(new APIError(jObjError.getString("msg")));
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            onRequestComplete.onAPIFailure(new APIError(AppConstants.SERVER_ERROR_MSG));
                        }
                    }
                });

            }

            @Override
            public void serverError(retrofit2.Response<?> response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onRequestComplete.onAPIFailure(new APIError(AppConstants.SERVER_ERROR_MSG));
                    }
                });

            }

            @Override
            public void networkError(IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onRequestComplete.onAPIFailure(new APIError(AppConstants.NETWORK_ERROR_MSG));
                    }
                });

            }

            @Override
            public void unexpectedError(final Throwable t) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onRequestComplete.onAPIFailure(new APIError(AppConstants.SERVER_ERROR_MSG));
                    }
                });

            }
        });
    }
}

