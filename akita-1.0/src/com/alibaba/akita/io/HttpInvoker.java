/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.widget.ProgressBar;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.util.ImageUtil;
import com.alibaba.akita.util.Log;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Http/Https Invoker
 * Get
 * Post(not idempotent) 
 * Put
 * Delete
 * Post With Files (URLConnection Impl)
 * @author zhe.yangz 2011-12-30 下午01:49:38
 */
public class HttpInvoker {
    private static String TAG = "HttpInvoker";
    private static String CHARSET = HTTP.UTF_8;
    
    private static ThreadSafeClientConnManager connectionManager;
    private static DefaultHttpClient client;
    
    static {
        init();
    }
    
    /**
     * init
     */
    private static void init() {
        
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(
                new Scheme("https", _FakeSSLSocketFactory.getSocketFactory(), 443));
        
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        HttpConnectionParams.setConnectionTimeout(params, 8000);
        HttpConnectionParams.setSoTimeout(params, 15000);
        params.setBooleanParameter("http.protocol.expect-continue", false);

        connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        client = new DefaultHttpClient(connectionManager, params);
        
        // enable gzip support in Request and Response. 
        client.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(
                    final HttpRequest request,
                    final HttpContext context) throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });
        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(
                    final HttpResponse response,
                    final HttpContext context) throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                //Log.i("ContentLength", entity.getContentLength()+"");
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(
                                    new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
        
    }

    public static String get(String url) throws AkServerStatusException, AkInvokeException {
        return get(url, null);
    }

    public static String get(String url, Header[] headers)
    throws AkServerStatusException, AkInvokeException {
        Log.v(TAG, "get:" + url);
        //url = url.replace("gw.api.alibaba.com", "172.20.128.127");
        //url = url.replace("mobi.aliexpress.com", "172.20.226.142");
        String retString = null;
        try {
            HttpGet request = new HttpGet(url);
            if (headers != null) {
                for (Header header : headers) {
                    request.addHeader(header);
                }
            }
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK
             || statusCode == HttpStatus.SC_CREATED
             || statusCode == HttpStatus.SC_ACCEPTED) {
                HttpEntity resEntity = response.getEntity();
                retString = (resEntity == null) ?
                    null : EntityUtils.toString(resEntity, CHARSET);
            } else {
                HttpEntity resEntity = response.getEntity();
                throw new AkServerStatusException(
                        response.getStatusLine().getStatusCode(),
                        EntityUtils.toString(resEntity, CHARSET));
            }
        } catch (ClientProtocolException cpe) {
            Log.e(TAG, cpe.toString(), cpe);
            throw new AkInvokeException(AkInvokeException.CODE_HTTP_PROTOCOL_ERROR,
                    cpe.toString(), cpe);
        } catch (IOException ioe) {
            Log.e(TAG, ioe.toString(), ioe);
            throw new AkInvokeException(AkInvokeException.CODE_CONNECTION_ERROR,
                    ioe.toString(), ioe);
        }

        Log.v(TAG, "response:" + retString);
        return retString;
    }

    public static String post(String url, ArrayList<NameValuePair> params)
            throws AkInvokeException, AkServerStatusException {
        //==log start
        // TEMP
        //url = url.replace("gw.api.alibaba.com", "172.20.128.127");
        //url = url.replace("mobi.aliexpress.com", "172.20.226.142");
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

        try {
            HttpPost request = new HttpPost(url);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, CHARSET);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK
             || statusCode == HttpStatus.SC_CREATED
             || statusCode == HttpStatus.SC_ACCEPTED) {
                HttpEntity resEntity = response.getEntity();
                retString = (resEntity == null) ?
                        null : EntityUtils.toString(resEntity, CHARSET);
            } else {
                HttpEntity resEntity = response.getEntity();
                throw new AkServerStatusException(
                        response.getStatusLine().getStatusCode(),
                        EntityUtils.toString(resEntity, CHARSET));
                
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString(), e);
            throw new AkInvokeException(
                    AkInvokeException.CODE_HTTP_PROTOCOL_ERROR, e.toString(), e);
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.toString(), e);
            throw new AkInvokeException(
                    AkInvokeException.CODE_HTTP_PROTOCOL_ERROR, e.toString(), e);
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
            throw new AkInvokeException(
                    AkInvokeException.CODE_CONNECTION_ERROR, e.toString(), e);
        } catch (ParseException e) {
            Log.e(TAG, e.toString(), e);
            throw new AkInvokeException(
                    AkInvokeException.CODE_PARSE_EXCEPTION, e.toString(), e);
        }

        Log.v(TAG, "response:" + retString);
        return retString;
    }
    
    public static String put(String url, HashMap<String, String> map) {
        return "";
    }
    
    public static String delete(String url) {
        return "";
    }

    private static final int DEFAULT_BUFFER_SIZE = 65536;
    private static byte[] retrieveImageData(InputStream inputStream, int fileSize, ProgressBar progressBar)
            throws IOException {

        // determine the remoteimageview size and allocate a buffer
        //Log.d(TAG, "fetching remoteimageview " + imgUrl + " (" +
        //        (fileSize <= 0 ? "size unknown" : Long.toString(fileSize)) + ")");
        BufferedInputStream istream = new BufferedInputStream(inputStream);

        try {
            if (fileSize <= 0) {
                android.util.Log.w(TAG,
                        "Server did not set a Content-Length header, will default to buffer size of "
                                + DEFAULT_BUFFER_SIZE + " bytes");
                ByteArrayOutputStream buf = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead = 0;
                while (bytesRead != -1) {
                    bytesRead = istream.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                    if (bytesRead > 0)
                        buf.write(buffer, 0, bytesRead);
                }
                return buf.toByteArray();
            } else {
                byte[] imageData = new byte[fileSize];

                int bytesRead = 0;
                int offset = 0;
                while (bytesRead != -1 && offset < fileSize) {
                    bytesRead = istream.read(imageData, offset, fileSize - offset);
                    offset += bytesRead;
                    // process reporting
                    try {
                        if (progressBar != null) {
                            progressBar.setProgress(offset*100/fileSize);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
                return imageData;
            }
        } finally {
            // clean up
            try {
                istream.close();
                inputStream.close();
            } catch (Exception ignore) { }
        }
    }

    private static final int NUM_RETRIES = 2;
    private static final int DEFAULT_RETRY_SLEEP_TIME = 1000;

    /**
     * Vversion 2 remoteimageview download impl, use byte[] to decode.
     * Note: Recommanded to use this method instead of version 1.
     * NUM_RETRIES retry.
     * @param imgUrl
     * @param httpReferer http Referer
     * @return
     * @throws AkServerStatusException
     * @throws AkInvokeException
     */
    public static Bitmap getBitmapFromUrl(String imgUrl, String httpReferer, ProgressBar progressBar)
    throws AkServerStatusException, AkInvokeException {
        imgUrl = imgUrl.trim();
        Log.v(TAG, "getBitmapFromUrl:" + imgUrl);

        int timesTried = 1;

        while (timesTried <= NUM_RETRIES) {
            timesTried++;
            try {
                if (progressBar != null) {
                    progressBar.setProgress(0);
                }
                HttpGet request = new HttpGet(imgUrl);
                if (httpReferer != null) request.addHeader("Referer", httpReferer);
                HttpResponse response = client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK
                        || statusCode == HttpStatus.SC_CREATED
                        || statusCode == HttpStatus.SC_ACCEPTED) {
                    HttpEntity resEntity = response.getEntity();
                    InputStream inputStream = resEntity.getContent();

                    byte[] imgBytes = retrieveImageData(
                            inputStream, (int)(resEntity.getContentLength()), progressBar);
                    if (imgBytes == null) {
                        SystemClock.sleep(DEFAULT_RETRY_SLEEP_TIME);
                        continue;
                    }

                    Bitmap bm = null;
                    try {
                        bm = ImageUtil.decodeSampledBitmapFromByteArray(
                                imgBytes, 0, imgBytes.length, 682, 682);
                    } catch (OutOfMemoryError ooe) {
                        Log.e(TAG, ooe.toString(), ooe);
                        return null; // if oom, no need to retry.
                    }
                    if (bm == null) {
                        SystemClock.sleep(DEFAULT_RETRY_SLEEP_TIME);
                        continue;
                    }
                    return bm;
                } else {
                    HttpEntity resEntity = response.getEntity();
                    throw new AkServerStatusException(
                            response.getStatusLine().getStatusCode(),
                            EntityUtils.toString(resEntity, CHARSET));
                }
            } catch (ClientProtocolException cpe) {
                Log.e(TAG, cpe.toString(), cpe);
                throw new AkInvokeException(AkInvokeException.CODE_HTTP_PROTOCOL_ERROR,
                        cpe.toString(), cpe);
            } catch (IOException ioe) {
                Log.e(TAG, ioe.toString(), ioe);
                throw new AkInvokeException(AkInvokeException.CODE_CONNECTION_ERROR,
                        ioe.toString(), ioe);
            } catch (IllegalStateException ise) {
                Log.e(TAG, ise.toString(), ise);
                throw new AkInvokeException(AkInvokeException.CODE_TARGET_HOST_OR_URL_ERROR,
                        ise.toString(), ise);
            } catch (IllegalArgumentException iae) {
                throw new AkInvokeException(AkInvokeException.CODE_TARGET_HOST_OR_URL_ERROR,
                        iae.toString(), iae);
            } catch (Exception e) {
                throw new  AkInvokeException(AkInvokeException.CODE_UNKOWN_ERROR, e.toString(), e);
            }

        }

        return null;
    }

    /**
     * version 1 remoteimageview download impl, use InputStream to decode.
     * @param imgUrl
     * @param inSampleSize
     * @return
     * @throws AkServerStatusException
     * @throws AkInvokeException
     */
    public static Bitmap getImageFromUrl(String imgUrl, int inSampleSize) 
    throws AkServerStatusException, AkInvokeException {
        Log.v(TAG, "getImageFromUrl:" + imgUrl);
        Bitmap bitmap = null;
        for (int cnt = 0; cnt < NUM_RETRIES; cnt++) {
            try {
                HttpGet request = new HttpGet(imgUrl);
                HttpResponse response = client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK
                        || statusCode == HttpStatus.SC_CREATED
                        || statusCode == HttpStatus.SC_ACCEPTED) {
                    HttpEntity resEntity = response.getEntity();
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        if (inSampleSize > 0 && inSampleSize < 10) {
                            options.inSampleSize = inSampleSize;
                        } else {
                            options.inSampleSize = 0;
                        }
                        InputStream inputStream = resEntity.getContent();

                        // return BitmapFactory.decodeStream(inputStream);
                        // Bug on slow connections, fixed in future release.
                        bitmap = BitmapFactory.decodeStream(new FlushedInputStream(
                                inputStream), null, options);
                    } catch (Exception e) {
                        e.printStackTrace();  //TODO Just for test
                        // no op
                    }
                    break;
                } else {
                    HttpEntity resEntity = response.getEntity();
                    throw new AkServerStatusException(
                            response.getStatusLine().getStatusCode(),
                            EntityUtils.toString(resEntity, CHARSET));
                }
            } catch (ClientProtocolException cpe) {
                Log.e(TAG, cpe.toString(), cpe);
                throw new AkInvokeException(AkInvokeException.CODE_HTTP_PROTOCOL_ERROR,
                        cpe.toString(), cpe);
            } catch (IOException ioe) {
                Log.e(TAG, ioe.toString(), ioe);
                throw new AkInvokeException(AkInvokeException.CODE_CONNECTION_ERROR,
                        ioe.toString(), ioe);
            }
        }
        return bitmap;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it
     * reaches EOF.
     */
    private static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }


    /**
     * post with files using URLConnection Impl
     * @param actionUrl URL to post
     * @param params params to post
     * @param files files to post, support multi-files
     * @return response in String format
     * @throws IOException
     */
    public static String postWithFilesUsingURLConnection(
            String actionUrl, ArrayList<NameValuePair> params, Map<String, File> files)
            throws AkInvokeException {
        try {
            String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";

            URL uri = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

            conn.setReadTimeout(60 * 1000);
            conn.setDoInput(true); // permit input
            conn.setDoOutput(true); // permit output
            conn.setUseCaches(false);
            conn.setRequestMethod("POST"); // Post Method
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);

            // firstly string params to add
            StringBuilder sb = new StringBuilder();
            for (NameValuePair nameValuePair : params) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + nameValuePair.getName() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(nameValuePair.getValue());
                sb.append(LINEND);
            }

            DataOutputStream outStream = new DataOutputStream(
                    conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // send files secondly
            if (files != null) {
                int num = 0;
                for (Map.Entry<String, File> file : files.entrySet()) {
                    num++;
                    if (file.getKey() == null || file.getValue() == null) continue;
                    else {
                        if (!file.getValue().exists()) {
                            throw new AkInvokeException(AkInvokeException.CODE_FILE_NOT_FOUND,
                                    "The file to upload is not found.");
                        }
                    }
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file"+num+"\"; filename=\""
                            + file.getKey() + "\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset="
                            + CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());

                    InputStream is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }

                    is.close();
                    outStream.write(LINEND.getBytes());
                }
            }

            // request end flag
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            // get response code
            int res = conn.getResponseCode();
            InputStream in = conn.getInputStream();
            StringBuilder sb2 = new StringBuilder();
            if (res == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in, "utf-8"),
                        8192);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb2.append(line + "\n");
                }
                reader.close();
            }
            outStream.close();
            conn.disconnect();
            return sb2.toString();
        } catch (IOException ioe) {
            throw new AkInvokeException(AkInvokeException.CODE_IO_EXCEPTION, "IO Exception", ioe);
        }
    }
    
}
