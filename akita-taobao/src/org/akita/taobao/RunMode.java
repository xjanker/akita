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
package org.akita.taobao;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-5-15
 * Time: PM1:58
 */
public enum RunMode {
    // 日常
    DALIY,

    // 预发
    PREDEPLOY,

    // 线上
    PRODUCTION
}
