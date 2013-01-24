/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

/**
 *  
 * @author zhe.yangz 2012-2-14 下午07:16:12
 */
class GzipDecompressingEntity extends HttpEntityWrapper {

    public GzipDecompressingEntity(final HttpEntity entity) {
        super(entity);
    }

    @Override
    public InputStream getContent()
        throws IOException, IllegalStateException {

        // the wrapped entity's getContent() decides about repeatability
        InputStream wrappedin = wrappedEntity.getContent();

        return new GZIPInputStream(wrappedin);
    }

    @Override
    public long getContentLength() {
        // length of ungzipped content is not known
        return -1;
    }

}
