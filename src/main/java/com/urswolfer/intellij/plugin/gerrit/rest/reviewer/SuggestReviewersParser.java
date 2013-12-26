/*
 * Copyright 2013 Urs Wolfer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urswolfer.intellij.plugin.gerrit.rest.reviewer;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.urswolfer.intellij.plugin.gerrit.rest.bean.SuggestedReviewer;
import com.urswolfer.intellij.plugin.gerrit.rest.bean.SuggestedReviewerInfo;

import static com.google.common.collect.Iterables.*;

/**
 * @author Thomas Forrer
 */
public class SuggestReviewersParser {
    private final Gson gson;

    @Inject
    public SuggestReviewersParser(Gson gson) {
        this.gson = gson;
    }

    private final Function<JsonElement, SuggestedReviewer> PARSER = new Function<JsonElement, SuggestedReviewer>() {
        @Override
        public SuggestedReviewer apply(JsonElement jsonElement) {
            SuggestedReviewerInfo suggestedReviewerInfo = gson.fromJson(jsonElement, SuggestedReviewerInfo.class);
            if (suggestedReviewerInfo.getAccount() != null) {
                return suggestedReviewerInfo.getAccount();
            } else if (suggestedReviewerInfo.getGroup() != null) {
                return suggestedReviewerInfo.getGroup();
            } else {
                return null;
            }
        }
    };

    public Iterable<SuggestedReviewer> parse(JsonElement jsonElement) {
        JsonArray array = jsonElement.getAsJsonArray();
        return filter(transform(array, PARSER), Predicates.<SuggestedReviewer>notNull());
    }
}
