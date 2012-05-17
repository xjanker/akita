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

package com.alibaba.akita.samples.api.top;

/**
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-4
 * Time: 上午12:46
 *
 * @author Justin Yang
 */
public class ItemGetResult {
    public ItemGetResponse item_get_response;
    public static class ItemGetResponse {
        public Item item;
        public static class Item {
            public int cid;
            public String desc;
            public String detail_url;
            public String nick;
            public long num_iid;
            public String seller_cids;
            public String title;
            public String type;
        }
    }
}
