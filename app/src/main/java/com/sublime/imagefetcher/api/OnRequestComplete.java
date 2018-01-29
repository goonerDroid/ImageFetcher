package com.sublime.imagefetcher.api;

/**
 * Created by goonerDroid
 * on 29-01-2018.
 */
public interface OnRequestComplete {

	void onSuccess(Object object);

	void onAPIFailure(APIError apiError);


}
