/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.researchstack.backbone.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by TheMDP on 11/21/17.
 */

public class ViewUtilsTest {

    Context context;

    @Before
    public void setUp() {
        context = Mockito.mock(Context.class);
    }

    @Test
    public void findEditTextTest_success() {
        EditText editText = new EditText(context);
        ViewGroup container = new MockViewGroup(context);
        container.addView(editText);
        assertNotNull(ViewUtils.findFirstEditText(container));
    }

    @Test
    public void findEditTextTest_successTwoDeepTwoElements() {
        EditText editText = new EditText(context);
        TextView textView = new TextView(context);
        TextView textView2 = new TextView(context);
        TextView textView3 = new TextView(context);
        ViewGroup container = new MockViewGroup(context);
        ViewGroup container2 = new MockViewGroup(context);
        container2.addView(textView2);
        container2.addView(textView3);
        container2.addView(editText);
        container.addView(textView);
        container.addView(container2);
        assertNotNull(ViewUtils.findFirstEditText(container));
    }

    @Test
    public void findEditTextTest_successTwoElements() {
        EditText editText = new EditText(context);
        TextView textView = new TextView(context);
        ViewGroup container = new MockViewGroup(context);
        container.addView(editText);
        container.addView(textView);
        assertNotNull(ViewUtils.findFirstEditText(container));
    }

    @Test
    public void findEditTextTest_fail() {
        TextView textView = new TextView(context);
        ViewGroup container = new MockViewGroup(context);
        container.addView(textView);
        assertNull(ViewUtils.findFirstEditText(container));
    }

    class MockViewGroup extends ViewGroup {

        private List<View> children;

        public MockViewGroup(Context context) {
            super(context);
            children = new ArrayList<>();
        }

        @Override
        public int getChildCount() {
            return children.size();
        }

        @Override
        public void addView(View view) {
            children.add(view);
        }

        @Override
        protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
            // no-op
        }

        @Override
        public View getChildAt(int i) {
            return children.get(i);
        }
    }
}
