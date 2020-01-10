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

package org.researchstack.backbone.ui.step.layout;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.utils.StepLayoutHelper;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StepLayoutHelper.class)
public class LoginStepLayoutTest {
    private static final String EMAIL = "example@example.com";
    private static final String EXTERNAL_ID = "dummy-external-id";
    private static final String PASSWORD = "dummy-password";

    private DataProvider mockDataProvider;
    private LoginStepLayout loginStepLayout;

    @Before
    public void setup() {
        // Use PowerMock to mock StepLayoutHelper
        mockStatic(StepLayoutHelper.class);

        // Mock DataProvider.
        mockDataProvider = mock(DataProvider.class);
        DataProvider.init(mockDataProvider);

        // LoginStepLayout is non-trivial to construct. Mock it and use callRealMethod() to test it.
        loginStepLayout = mock(LoginStepLayout.class);
    }

    @AfterClass
    public static void teardown() {
        // De-init DataProvider, so that it doesn't interfere with other tests.
        DataProvider.init(null);
    }

    @Test
    public void onNextClicked_EmailPassword() {
        // Set up mocks for LoginStepLayout
        when(loginStepLayout.isAnswerValid(any(), anyBoolean())).thenReturn(true);
        when(loginStepLayout.getEmail()).thenReturn(EMAIL);
        when(loginStepLayout.getPassword()).thenReturn(PASSWORD);
        when(loginStepLayout.getExternalId()).thenReturn(null);
        doCallRealMethod().when(loginStepLayout).onNextClicked();

        // Mock DataProvider.signIn()
        Observable<DataResponse> loginResult = Observable.empty();
        when(mockDataProvider.signIn(any(), any(), any())).thenReturn(loginResult);

        // Execute
        loginStepLayout.onNextClicked();

        // Verify dependencies.
        verify(mockDataProvider).signIn(any(), eq(EMAIL), eq(PASSWORD));
        verify(mockDataProvider, never()).signInWithExternalId(any(), any());

        verifyStatic();
        StepLayoutHelper.safePerform(same(loginResult), any(), any());
    }

    @Test
    public void onNextClicked_ExternalId() {
        // Set up mocks for LoginStepLayout
        when(loginStepLayout.isAnswerValid(any(), anyBoolean())).thenReturn(true);
        when(loginStepLayout.getEmail()).thenReturn(null);
        when(loginStepLayout.getPassword()).thenReturn(null);
        when(loginStepLayout.getExternalId()).thenReturn(EXTERNAL_ID);
        doCallRealMethod().when(loginStepLayout).onNextClicked();

        // Mock DataProvider.signIn()
        Observable<DataResponse> loginResult = Observable.empty();
        when(mockDataProvider.signInWithExternalId(any(), any())).thenReturn(loginResult);

        // Execute
        loginStepLayout.onNextClicked();

        // Verify dependencies.
        verify(mockDataProvider, never()).signIn(any(), any(), any());
        verify(mockDataProvider).signInWithExternalId(any(), eq(EXTERNAL_ID));

        verifyStatic();
        StepLayoutHelper.safePerform(same(loginResult), any(), any());
    }

    @Test
    public void onNextClicked_NoCredentials() {
        // Set up mocks for LoginStepLayout
        when(loginStepLayout.isAnswerValid(any(), anyBoolean())).thenReturn(true);
        when(loginStepLayout.getEmail()).thenReturn(null);
        when(loginStepLayout.getPassword()).thenReturn(null);
        when(loginStepLayout.getExternalId()).thenReturn(null);
        doCallRealMethod().when(loginStepLayout).onNextClicked();

        // Execute
        loginStepLayout.onNextClicked();

        // Verify dependencies.
        verifyZeroInteractions(mockDataProvider);

        verifyStatic(never());
        StepLayoutHelper.safePerform(any(), any(), any());
    }
}
