package org.akita.io;

import org.akita.exception.AkInvokeException;
import org.akita.exception.AkServerStatusException;
import org.akita.util.Log;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * HttpInvoker2 using UrlConnection
 * Created by justin on 15/11/26.
 */
public class HttpInvoker2 {
    private static final String TAG = HttpInvoker2.class.getSimpleName();
    public static int CONNECT_TIME_OUT = 8 * 1000;
    public static int READ_TIME_OUT = 15 * 1000;

    public static String get(String url) throws AkServerStatusException, AkInvokeException {
        return get(url, null);
    }
    public static String get(String url, Header[] headers)
            throws AkServerStatusException, AkInvokeException {
        Log.v(TAG, "get:" + url);
        String retString = null;
        HttpURLConnection urlConnection = null;
        try {
            URL thisUrl = new URL(url);
            urlConnection = (HttpURLConnection) thisUrl.openConnection();
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            if (headers != null && headers.length > 0) {
                for (Header header : headers) {
                    urlConnection.setRequestProperty(header.getName(), header.getValue());
                }
            }

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK
                    || statusCode == HttpURLConnection.HTTP_CREATED
                    || statusCode == HttpURLConnection.HTTP_ACCEPTED ) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                retString = readInStream(in);
            } else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                throw new AkServerStatusException(
                        statusCode,
                        readInStream(in));
            }
        } catch (IOException ioe) {
            Log.e(TAG, ioe.toString(), ioe);
            throw new AkInvokeException(AkInvokeException.CODE_CONNECTION_ERROR,
                    ioe.toString(), ioe);
        } catch (Exception e) {
            throw new AkInvokeException(AkInvokeException.CODE_CONNECTION_ERROR,
                    e.toString(), e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }


        Log.v(TAG, "response:" + retString);
        return retString;
    }

    private static String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public static String post(String url, ArrayList<NameValuePair> params)
            throws AkInvokeException, AkServerStatusException {
        return post(url, params, null);
    }

    public static String post(String url, ArrayList<NameValuePair> params, Header[] headers)
            throws AkInvokeException, AkServerStatusException {
        //==log start
        Log.v(TAG, "post:" + url);
        if (params != null) {
            Log.v(TAG, "params:=====================");
            for (NameValuePair nvp : params) {
                Log.v(TAG, nvp.getName() + "=" + nvp.getValue());
            }
            Log.v(TAG, "params end:=====================");
        }
        //==log end

        String retString = null;
        HttpURLConnection urlConnection = null;

        try {
            URL thisUrl = new URL(url);
            urlConnection = (HttpURLConnection) thisUrl.openConnection();
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            if (headers != null && headers.length > 0) {
                for (Header header : headers) {
                    urlConnection.setRequestProperty(header.getName(), header.getValue());
                }
            }

            if (params != null && params.size() > 0) {
                // Post传参
                StringBuilder sbParam = new StringBuilder("");
                for (NameValuePair nameValuePair : params) {
                    sbParam.append(nameValuePair.getName()).append("=").append(nameValuePair.getValue());
                    sbParam.append("&");
                }
                urlConnection.getOutputStream().write(sbParam.toString().getBytes());
            }

            int statusCode = urlConnection.getResponseCode();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            if (statusCode == HttpURLConnection.HTTP_OK
                    || statusCode == HttpURLConnection.HTTP_CREATED
                    || statusCode == HttpURLConnection.HTTP_ACCEPTED ) {
                retString = readInStream(in);
            } else {
                throw new AkServerStatusException(
                        statusCode, readInStream(in));
            }
        } catch (IOException ioe) {
            Log.e(TAG, ioe.toString(), ioe);
            throw new AkInvokeException(AkInvokeException.CODE_CONNECTION_ERROR,
                    ioe.toString(), ioe);
        } catch (Exception e) {
            throw new AkInvokeException(AkInvokeException.CODE_CONNECTION_ERROR,
                    e.toString(), e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        Log.v(TAG, "response:" + retString);
        return retString;
    }
}
