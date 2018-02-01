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

package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 11/17/17.
 */

public class TextfieldSurveyItem extends QuestionSurveyItem<String> {

    @SerializedName("inputType")
    public Integer inputType;

    @SerializedName("validationRegex")
    public String validationRegex;

    @SerializedName("disabled")
    public Boolean disabled;

    @SerializedName("isMultipleLines")
    public Boolean isMultipleLines;

    @SerializedName("maxLength")
    public Integer maxLength;

    /* Default constructor needed for serialization/deserialization of object */
    public TextfieldSurveyItem() {
        super();
    }
}
