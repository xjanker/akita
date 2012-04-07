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

package com.alibaba.akita.samples.api.weibo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-4
 * Time: 上午12:46
 *
 * @author Justin Yang
 */
public class Statuses {
    public ArrayList<StatusWrap> statuses;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusWrap {
        public Status status;
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Status {
            public String text;
            public String original_pic;
        }

        public String pid;
        public int pwidth;
        public int pheight;
        public int is_pic;
    }

    public int total_number;
}
