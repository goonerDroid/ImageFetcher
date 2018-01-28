package com.sublime.imagefetcher.api;


public interface RetrofitCall<T> {

    void cancel();

    RetrofitCall<T> clone();

    void enqueue(RetrofitCallback<Object> msg);
}
