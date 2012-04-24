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

package com.alibaba.akita.samples.api;

import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.annotation.AkAPI;
import com.alibaba.akita.annotation.AkParam;
import com.alibaba.akita.annotation.AkGET;
import com.alibaba.akita.samples.api.weibo.Statuses;

/**
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-4
 * Time: 上午12:36
 *
 * @author Justin Yang
 */
public interface WeiboApi {
    @AkGET
    @AkAPI(url="https://api.weibo.com/2/suggestions/statuses/hot.json?access_token=2.00XjaxYB7hhCID89eea9a39bhZnoTC&")
    Statuses suggestions_statuses_hot(
            @AkParam("type") int type,       //微博精选分类，1：娱乐、2：搞笑、3：美女、4：视频、5：星座、6：各种萌、7：时尚、8：名车、9：美食、10：音乐。
            @AkParam("is_pic") int is_pic,   //0：全部、1：图片微博。
            @AkParam("count") int count,     //count default 20
            @AkParam("page") int page        //page default 1
    ) throws AkInvokeException, AkServerStatusException;
}
