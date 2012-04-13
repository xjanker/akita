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

package com.alibaba.akita.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.alibaba.akita.R;

/**
 * Simple Loading Dialog @ akia lib
 *
 * Created with IntelliJ IDEA.
 * Date: 12-4-11
 * Time: 上午10:22
 *
 * @author zhe.yangz
 */
public class SimpleLoadingDialog extends Dialog {
    public SimpleLoadingDialog(Context context, String loadingHint) {
        super(context, R.style.LoadingDialog1);
        setContentView(R.layout.dlg_loading1);
        setCancelable(false);

        if (loadingHint != null && !loadingHint.isEmpty()) {
            TextView tv_loadingHint = (TextView) findViewById(R.id.tv_loadingHint);
            tv_loadingHint.setText(loadingHint);
            tv_loadingHint.setVisibility(View.VISIBLE);
        }
    }
}
