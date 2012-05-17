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

package com.alibaba.akita.samples;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import com.alibaba.akita.widget.TitlePageIndicator;

public class SampleTitlesCenterClickListener extends BaseSampleActivity implements TitlePageIndicator.OnCenterItemClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_titles);
		
		mAdapter = new TestTitleFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Underline);
		indicator.setOnCenterItemClickListener(this);
        mIndicator = indicator;
	}

    @Override
    public void onCenterItemClick(int position) {
        Toast.makeText(this, "You clicked the center title!", Toast.LENGTH_SHORT).show();
    }
}