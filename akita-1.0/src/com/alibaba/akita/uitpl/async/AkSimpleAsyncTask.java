package com.alibaba.akita.uitpl.async;

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
public abstract class AkSimpleAsyncTask<T> extends AsyncTask<Void, Void, T> {

    private static final String TAG = "AkSimpleAsyncTask<T>";
    protected AkException mAkException = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //defaults
        onUIBefore();
    }

    @Override
    protected T doInBackground(Void... voids) {
        try {
            return onDoAsync();
        } catch (AkException akException) {
            mAkException = akException;
            return null;
        }
    }

    protected abstract void onUIBefore();
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


    }

    protected void onHandleAkException(AkException mAkException) {
        Log.e(TAG, mAkException.toString(), mAkException);
    }

}
