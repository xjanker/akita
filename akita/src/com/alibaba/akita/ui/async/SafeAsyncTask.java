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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //defaults

        try{
            onUIBefore();
        } catch (Exception akException) {
            mException = akException;
        }
    }

    public AsyncTask<Integer, Integer, T> fire() {
        return execute(0);
    }

    public AsyncTask<Integer, Integer, T> fireOnParallel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
        } else {
            return execute(0);
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

    /**
     * SimpleAsyncTask中的onUITaskStart()方法在此类中取消，一律用onUIBefore()。
     * @throws Exception
     */
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
                onHandleAkException(akException);
            }
        }
        try {
            onUITaskEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled(T t) {
        try {
            onUITaskEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onHandleAkException(Exception mAkException) {
        Log.w(TAG, mAkException.toString(), mAkException);

        if (mContext != null) {
            Toast.makeText(mContext, mAkException.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * guarantees the method be invoked on ui thread once time when task quit.
     * if this method meet the exception, then return and no error, the code after executed-part is not called.
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
