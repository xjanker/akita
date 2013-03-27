package com.alibaba.akita.ui.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import com.alibaba.akita.util.Log;

/**
 * 与SimpleAsyncTask的区别就是，任意Exception都能处理。
 * Date: 12-4-9
 * Time: 上午11:20
 *
 * @author zhe.yangz
 */
public abstract class SafeAsyncTask<T> extends AsyncTask<Integer, Integer, T> {
    private static final String TAG = "SafeAsyncTask<T>";
    protected Exception mException = null;
    private Context mContext = null;

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
        } catch (Exception akException) {
            mException = akException;
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

    public AsyncTask<Integer, Integer, T> fire(Context context) {
        mContext = context;
        return fire();
    }

    public AsyncTask<Integer, Integer, T> fireOnParallel(Context context) {
        mContext = context;
        return fireOnParallel();
    }

    @Override
    protected T doInBackground(Integer... integers) {
        try {
            if (mException == null) {
                return onDoAsync();
            } else {
                return null;
            }
        } catch (Exception akException) {
            mException = akException;
            return null;
        }
    }

    protected abstract void onUIBefore() throws Exception;
    protected abstract T onDoAsync() throws Exception;
    /**
     * it may not be executed if have exception before.
     * @param t
     * @throws com.alibaba.akita.exception.AkException
     */
    protected abstract void onUIAfter(T t) throws Exception;

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);    //defaults

        if (mException != null) {
            onHandleAkException(mException);
        } else {
            try {
                onUIAfter(t);
            } catch (Exception akException) {
                onHandleAkException(mException);
            }
        }
        onUITaskEnd();
    }

    @Override
    protected void onCancelled(T t) {
        onUITaskEnd();
    }

    protected void onHandleAkException(Exception mAkException) {
        Log.e(TAG, mAkException.toString(), mAkException);

        if (mContext != null) {
            Toast.makeText(mContext, mAkException.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * guarantees the method be invoked on ui thread once time when task quit.
     */
    protected void onUITaskEnd() {};

    /**
     * public of the method publishProgress, but must also be called in doinbackground.
     * @param values
     */
    public void publishProgressPublic(Integer... values) {
        publishProgress(values);
    }

}
