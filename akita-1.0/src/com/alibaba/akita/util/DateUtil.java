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

package com.alibaba.akita.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Date: 12-4-23
 * Time: 下午2:35
 *
 * @author zhe.yangz
 */
public class DateUtil {
    /**
     *
     * @param milliseconds 毫秒数
     * @return 简单的默认格式日期时间
     */
    public static String getSimpleDatetime(long milliseconds) {
        Date date = new Date(milliseconds);
        DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
        return sdf.format(date);
    }

    /**
     * "yyyy-MM-dd HH:mm:ss Z"
     * @param milliseconds 毫秒数
     * @return 默认上述格式的时间
     */
    public static String getDefaultDatetime(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        return sdf.format(date);
    }

    /**
     * "20100315072741000-0700" =>  "yyyy-MM-dd HH:mm:ss Z"
     * ex. 可用于Ocean返回的时间格式的转换
     * @param strTime
     * @return
     */
    public static String getTimeString(String strTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSZ");
            Date d = formatter.parse(strTime);
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

            return df1.format(d);
        } catch (ParseException e) {
            return strTime;
        } catch (NullPointerException npe) {
            return "";
        }
    }
}
