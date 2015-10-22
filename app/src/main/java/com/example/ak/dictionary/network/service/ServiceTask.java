package com.example.ak.dictionary.network.service;

import android.os.AsyncTask;

import java.util.concurrent.Callable;

/**
 * Async request
 */
public class ServiceTask<T> extends AsyncTask<Void, Void, Object> {

    public interface ServiceListener<T> {
        void onTaskComplete(T result);
        void onTaskError(Exception error);
    }

    ServiceListener<T> mListener;
    Callable<T> mCallable;
    Class<T> mResultType;

	public ServiceTask(Class<T> resultType, Callable<T> callable) {
		super();
        mCallable = callable;
        mResultType = resultType;
	}

    public void setListener(ServiceListener<T> mListener) {
        this.mListener = mListener;
    }

	@Override
	protected final Object doInBackground(Void... arg0) {
		try {
			return mCallable.call();
		}
		catch (Exception e) {
            return e;
        }
	}

    @Override
	protected final void onPostExecute(Object result) {
    	super.onPostExecute(result);

		if (mResultType.isAssignableFrom(result.getClass())) {
            if (mListener != null) {
                mListener.onTaskComplete((T) result);
            }
		}

        if (result instanceof Exception) {
            if (mListener != null) {
                mListener.onTaskError((Exception) result);
            }
        }
    }
}
