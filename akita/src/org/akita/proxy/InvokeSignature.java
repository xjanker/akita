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
package org.akita.proxy;

import java.util.ArrayList;
import java.util.HashMap;

import org.akita.annotation.AkSignature;
import org.apache.http.NameValuePair;


/**
 * interface of the signature class 
 * @author zhe.yangz 2012-2-17 下午08:18:10
 */
public interface InvokeSignature {
    public String getSignatureParamName();
    public String signature(AkSignature akSig, String invokeUrl,
                            ArrayList<NameValuePair> params, HashMap<String, String> paramsMapOri);
}
