package org.researchstack.backbone.utils;

import org.junit.Test;
import org.researchstack.backbone.task.factory.TaskExcludeOption;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created by TheMDP on 3/7/17.
 */

public class OptionSetUtilsTests {

    @Test
    public void testTaskExcludeOptions() {
        {
            List<TaskExcludeOption> expectedResult = Arrays.asList(
                    TaskExcludeOption.INSTRUCTIONS,
                    TaskExcludeOption.CONCLUSION);

            int optionSetForResult = (0x1) | (0x1 << 1);
            List<Enum> enumList = OptionSetUtils.toEnumList(optionSetForResult, TaskExcludeOption.values());

            assertNotNull(enumList);
            assertEquals(enumList.size(), expectedResult.size());
            for (int i = 0; i < enumList.size(); i++) {
                assertEquals(enumList.get(i), expectedResult.get(i));
            }
        }

        {
            List<TaskExcludeOption> expectedResult = Arrays.asList(
                    TaskExcludeOption.DEVICE_MOTION,
                    TaskExcludeOption.AUDIO);

            int optionSetForResult = (0x1 << 3) | (0x1 << 7);
            List<Enum> enumList = OptionSetUtils.toEnumList(optionSetForResult, TaskExcludeOption.values());

            assertNotNull(enumList);
            assertEquals(enumList.size(), expectedResult.size());
            for (int i = 0; i < enumList.size(); i++) {
                assertEquals(enumList.get(i), expectedResult.get(i));
            }
        }
    }
}
