/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akita.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * 解决调用Https服务时安全证书不可信的问题。
 */
public class _FakeSSLSocketFactory implements LayeredSocketFactory {

    private static final _FakeSSLSocketFactory DEFAULT_FACTORY = new _FakeSSLSocketFactory();

    public static _FakeSSLSocketFactory getSocketFactory() {
        return DEFAULT_FACTORY;
    }

    private SSLContext                     sslcontext;
    private javax.net.ssl.SSLSocketFactory socketfactory;

    private _FakeSSLSocketFactory(){
        super();
        TrustManager[] tm = new TrustManager[] { new _FakeX509TrustManager() };
        try {
            this.sslcontext = SSLContext.getInstance(SSLSocketFactory.TLS);
            this.sslcontext.init(null, tm, new SecureRandom());
            this.socketfactory = this.sslcontext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyManagementException e) {
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
                                                                                       UnknownHostException {
        SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(socket, host, port, autoClose);
        return sslSocket;
    }

    @Override
    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort,
                                HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }

        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

        if ((localAddress != null) || (localPort > 0)) {
            if (localPort < 0) {
                localPort = 0;
            }

            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
            sslsock.bind(isa);
        }

        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress;
        remoteAddress = new InetSocketAddress(host, port);

        sslsock.connect(remoteAddress, connTimeout);

        sslsock.setSoTimeout(soTimeout);

        return sslsock;
    }

    @Override
    public Socket createSocket() throws IOException {
        return (SSLSocket) this.socketfactory.createSocket();
    }

    @Override
    public boolean isSecure(Socket sock) throws IllegalArgumentException {
        return true;
    }

}
