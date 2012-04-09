package com.alibaba.akita.uitpl.async;

import android.os.AsyncTask;

/**
 * Created with IntelliJ IDEA.
 * Date: 12-4-9
 * Time: 上午11:20
 *
 * @author zhe.yangz
 */
public abstract class AkSimpleAsyncTask<T> extends AsyncTask<Void, Void, T> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //defaults
        onUIBefore();
    }

    @Override
    protected T doInBackground(Void... voids) {
        return onDoAsync();
    }

    protected abstract void onUIBefore();
    protected abstract T onDoAsync();
    protected abstract void onUIAfter(T t);

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);    //defaults
        onUIAfter(t);
    }

}
