package com.alibaba.akita.ui.async;

import android.os.AsyncTask;
import android.os.Build;
import com.alibaba.akita.exception.AkException;
import com.alibaba.akita.util.Log;

/**
 * Created with IntelliJ IDEA.
 * Date: 12-4-9
 * Time: 上午11:20
 *
 * @author zhe.yangz
 */
public abstract class SimpleAsyncTask<T> extends AsyncTask<Integer, Integer, T> {
    private static final String TAG = "SimpleAsyncTask<T>";
    protected AkException mAkException = null;

    /**
     * guarantees the method be invoked on ui thread once time when task start.
     */
    protected void onUITaskStart() {};

    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //defaults

        onUITaskStart();
        try{
            onUIBefore();
        } catch (AkException akException) {
            mAkException = akException;
        }
    }

    public AsyncTask<Integer, Integer, T> fire() {
        return execute(new Integer[] {0});
    }

    public AsyncTask<Integer, Integer, T> fireOnParallel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{0});
        } else {
            return execute(new Integer[] {0});
        }
    }

    @Override
    protected T doInBackground(Integer... integers) {
        try {
            if (mAkException == null) {
                return onDoAsync();
            } else {
                return null;
            }
        } catch (AkException akException) {
            mAkException = akException;
            return null;
        }
    }

    protected abstract void onUIBefore() throws AkException;
    protected abstract T onDoAsync() throws AkException;
    protected abstract void onUIAfter(T t) throws AkException;

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);    //defaults

        if (mAkException != null) {
            onHandleAkException(mAkException);
        } else {
            try {
                onUIAfter(t);
            } catch (AkException akException) {
                onHandleAkException(mAkException);
            }
        }
        onUITaskEnd();
    }

    @Override
    protected void onCancelled(T t) {
        onUITaskEnd();
    }

    protected void onHandleAkException(AkException mAkException) {
        Log.e(TAG, mAkException.toString(), mAkException);
    }

    /**
     * guarantees the method be invoked on ui thread once time when task quit.
     */
    protected void onUITaskEnd() {};

}
