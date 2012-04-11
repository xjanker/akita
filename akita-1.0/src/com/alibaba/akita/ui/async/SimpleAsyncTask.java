package com.alibaba.akita.ui.async;

import android.os.AsyncTask;
import com.alibaba.akita.exception.AkException;
import com.alibaba.akita.util.Log;

/**
 * Created with IntelliJ IDEA.
 * Date: 12-4-9
 * Time: 上午11:20
 *
 * @author zhe.yangz
 */
public abstract class SimpleAsyncTask<T> extends AsyncTask<Void, Void, T> {

    private static final String TAG = "SimpleAsyncTask<T>";
    protected AkException mAkException = null;

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

    @Override
    protected T doInBackground(Void... voids) {
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
     * guarantees the method be invoked on ui thread once time when task start.
     */
    protected abstract void onUITaskStart();

    /**
     * guarantees the method be invoked on ui thread once time when task quit.
     */
    protected abstract void onUITaskEnd();

}
