package org.researchstack.backbone.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 3/7/17.
 */

public class OptionSetUtils {

    /**
     * @param optionSetBitMask the the bit mask representing a list of enum options
     * @param enumOptions this should always be the result of your Enum.values();
     * @param <E> the type of the enum
     * @return a list of enums that are represented by the optionSet
     */
    public static <E extends Enum> List<E> toEnumList(int optionSetBitMask, E[] enumOptions) {
        List<E> enumList = new ArrayList<>();
        for (int i = 0; i < enumOptions.length; i++) {
            if ((optionSetBitMask & (0x1 << i)) != 0) {
                enumList.add(enumOptions[i]);
            }
        }
        return enumList;
    }
}
