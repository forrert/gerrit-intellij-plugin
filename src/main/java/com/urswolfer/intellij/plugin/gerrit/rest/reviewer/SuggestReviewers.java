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

import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import com.urswolfer.intellij.plugin.gerrit.rest.ConsumerResult;
import com.urswolfer.intellij.plugin.gerrit.rest.GerritRestAccess;
import com.urswolfer.intellij.plugin.gerrit.rest.bean.SuggestedReviewer;
import com.urswolfer.intellij.plugin.gerrit.util.NotificationBuilder;
import com.urswolfer.intellij.plugin.gerrit.util.NotificationService;

/**
 * @author Thomas Forrer
 */
public class SuggestReviewers {
    private final GerritRestAccess gerritRestAccess;
    private final SuggestReviewersParser suggestReviewersParser;
    private final NotificationService notificationService;

    @Inject
    public SuggestReviewers(GerritRestAccess gerritRestAccess,
                            SuggestReviewersParser suggestReviewersParser,
                            NotificationService notificationService) {
        this.gerritRestAccess = gerritRestAccess;
        this.suggestReviewersParser = suggestReviewersParser;
        this.notificationService = notificationService;
    }

    private static final String REQUEST_STRING_PATTERN = "/a/changes/%s/suggest_reviewers?q=%s";

    public void suggestReviewers(final String changeId,
                                 final Project project,
                                 final Consumer<Iterable<SuggestedReviewer>> consumer,
                                 final String query) {
        String request = buildRequest(changeId, query);
        gerritRestAccess.getRequest(request, project, new Consumer<ConsumerResult<JsonElement>>() {
            @Override
            public void consume(ConsumerResult<JsonElement> consumerResult) {
                if (consumerResult.getException().isPresent()) {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    NotificationBuilder notification = new NotificationBuilder(
                            project,
                            "Failed to suggest reviewers",
                            consumerResult.getException().get().getMessage());
                    notificationService.notifyWarning(notification);
                } else {
                    Iterable<SuggestedReviewer> reviewerInfo = suggestReviewersParser.parse(
                            consumerResult.getResult());
                    consumer.consume(reviewerInfo);
                }
            }
        });
    }

    private String buildRequest(String changeId, String query) {
        return String.format(REQUEST_STRING_PATTERN, changeId, query);
    }
}
