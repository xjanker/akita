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
 * the root exception of Akita lib. 
 * @author zhe.yangz 2012-1-17 下午06:57:53
 */
public class AkException extends Exception {
    private static final long serialVersionUID =  -2431196726844826744L;
    
    protected AkException(){
        super();
    }

    protected AkException(Throwable t){
        super(t);
    }
    
    public AkException(String msg){
        super(msg);
    }
    
    public AkException(String msg, Throwable t){
        super(msg,t);
    }
}
