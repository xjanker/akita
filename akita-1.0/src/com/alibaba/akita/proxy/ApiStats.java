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

package com.alibaba.akita.proxy;

import com.alibaba.akita.cache.AkCacheManager;
import com.alibaba.akita.cache.MemCache;
import com.alibaba.akita.util.DateUtil;

import java.util.Map;

/**
 * Record Raw Api invocation history.
 * Created with IntelliJ IDEA.
 * Date: 12-4-23
 * Time: 下午12:57
 *
 * @author zhe.yangz
 */
public class ApiStats {

    private static MemCache<Long, ApiInvokeInfo> sRecentInvocations =
            AkCacheManager.newMemLruCache(100);

    /**
     * Only effect on API level 12+, because of android.util.LruCache
     * @param aii
     */
    public static void addApiInvocation(ApiInvokeInfo aii) {
        long now = System.currentTimeMillis();
        aii.invokeTime = DateUtil.getSimpleDatetime(now);
        sRecentInvocations.put(now, aii);
    }

    public static Map<Long, ApiInvokeInfo> getApiInvocationSnapShot() {
        return sRecentInvocations.snapshot();
    }
}
