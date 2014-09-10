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
package org.akita.exception;

import java.io.IOException;

import org.akita.util.JsonMapper;
import org.akita.util.Log;
import org.codehaus.jackson.JsonParseException;



/**
 * server error such as error(500) or exception(401 403) or (1001 TOP error) or etc
 * @author zhe.yangz 2012-1-17 下午06:57:53
 */
public class AkServerStatusException extends AkException{
    private static final long serialVersionUID = 8831634121316777078L;
    private static final String TAG = "AkServerStatusException";

    public static final int CODE_TOP_ERROR = 1001;

    /**
     * exception code
     */
    public int code;
    
    /**
     * get the server error detail
     * @param <T>
     * @param clazz
     * @return null if exception, otherwise errorMsg T
     * @throws IOException 
     * @throws JsonParseException
     */
    public <T> T getServerError(Class<T> clazz) {
        try {
            return JsonMapper.json2pojo(getMessage(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parse error:"+getMessage());
            return null;
        }
    }

    @SuppressWarnings("unused")
    private AkServerStatusException(){
        super();
    }
    @SuppressWarnings("unused")
    private AkServerStatusException(Throwable t){
        super(t);
    }
    
    public AkServerStatusException(int code, String msg){
        super(msg);
        
        this.code = code;
    }
    
    public AkServerStatusException(int code, String msg, Throwable t){
        super(msg,t);
        
        this.code = code;
    }
    


}
