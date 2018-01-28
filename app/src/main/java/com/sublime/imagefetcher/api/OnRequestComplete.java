package com.sublime.imagefetcher.api;

/**
 * Created by bhushan on 3/12/15.
 *
 * @author Bhushan
 */
public interface OnRequestComplete {

	void onSuccess(Object object);

	void onAPIFailure(APIError apiError);


}
