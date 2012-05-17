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

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.alibaba.akita.widget.viewpagerindicator.PageIndicator;

import java.util.Random;

public abstract class BaseSampleActivity extends FragmentActivity {
	private static final Random RANDOM = new Random();
	
	TestFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.random:
				final int page = RANDOM.nextInt(mAdapter.getCount());
				Toast.makeText(this, "Changing to page " + page, Toast.LENGTH_SHORT);
				mPager.setCurrentItem(page);
				return true;
				
			case R.id.add_page:
				if (mAdapter.getCount() < 10) {
					mAdapter.setCount(mAdapter.getCount() + 1);
					mIndicator.notifyDataSetChanged();
				}
				return true;
				
			case R.id.remove_page:
				if (mAdapter.getCount() > 1) {
					mAdapter.setCount(mAdapter.getCount() - 1);
					mIndicator.notifyDataSetChanged();
				}
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
