/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.akita.exception;

import android.content.Context;
import com.alibaba.akita.util.Log;

/**
 * Basic ExceptionHandler, toast the exception
 * @author zhe.yangz 2012-2-17 上午11:39:58
 */
public class BasicExceptionHandler {
    private static final String TAG = "BasicExceptionHandler";

    protected Context mContext = null;

    public BasicExceptionHandler(Context c) {
        mContext = c;
    }

    public void handle(AkException e) {
        if (e instanceof AkInvokeException) {
            Log.e(TAG, ((AkInvokeException) e).code + " " + e.getCause().toString(), e);
        }
        else if (e instanceof AkServerStatusException) {
            Log.e(TAG, ((AkServerStatusException) e).code + " " + e.toString(), e);
        }
        else {
            Log.e(TAG, e.toString(), e);
        }
    }

}
