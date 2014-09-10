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

/**
 * the exception caused when invoke a rpc, 
 * and server not return correct or connection lost. 
 * @author zhe.yangz 2012-1-17 下午06:57:53
 */
public class AkInvokeException extends AkException {
    private static final long serialVersionUID = -2431196726844826744L;
   
    public static final int CODE_CONNECTION_ERROR = 1000;
    public static final int CODE_HTTP_PROTOCOL_ERROR = 1001;
    public static final int CODE_UNSUPPORT_ENCODING = 1002;
    public static final int CODE_PARSE_EXCEPTION = 1003;
    public static final int CODE_JSONPROCESS_EXCEPTION = 1004;
    public static final int CODE_IO_EXCEPTION = 1005;
    public static final int CODE_FULFILL_INVOKE_EXCEPTION = 1006;
    public static final int CODE_PARAM_IN_URL_NOT_FOUND = 1007;
    public static final int CODE_FILE_NOT_FOUND = 1008;
    public static final int CODE_TARGET_HOST_OR_URL_ERROR = 1009;
    public static final int CODE_REQUEST_FIELD_EXCEPTION = 1010;
    public static final int CODE_POST_PARAM_NULL_ERROR = 1011;

    public static final int CODE_UNKOWN_ERROR = 1099;

    /**
     * exception code
     */
    public int code;

    @SuppressWarnings("unused")
    private AkInvokeException(){
        super();
    }
    @SuppressWarnings("unused")
    private AkInvokeException(Throwable t){
        super(t);
    }
    
    public AkInvokeException(int code, String msg){
        super(msg);
        
        this.code = code;
    }
    
    public AkInvokeException(int code, String msg, Throwable t){
        super(msg,t);
        
        this.code = code;
    }

    @Override
    public String toString() {
        return "["+code+"] "+super.toString();    //defaults
    }
}
