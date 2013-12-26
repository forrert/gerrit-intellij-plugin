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

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.urswolfer.intellij.plugin.gerrit.rest.bean.AccountInfo;
import com.urswolfer.intellij.plugin.gerrit.rest.bean.GroupInfo;
import com.urswolfer.intellij.plugin.gerrit.rest.bean.SuggestedReviewer;
import com.urswolfer.intellij.plugin.gerrit.rest.gson.DateDeserializer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;
import static org.testng.Assert.assertEquals;

/**
 * @author Thomas Forrer
 */
public class SuggestReviewersParserTest {
    private final Supplier<Gson> gson = Suppliers.memoize(new Supplier<Gson>() {
        @Override
        public Gson get() {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new DateDeserializer());
            builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            return builder.create();
        }
    });

    private final SuggestReviewersParser suggestReviewersParser = new SuggestReviewersParser(gson.get());

    @Test(dataProvider = "resources")
    public void testParser(TestCase testCase) throws Exception {
        JsonElement jsonElement = getJsonElement(testCase.fileName);
        Iterable<SuggestedReviewer> suggestedReviewers = suggestReviewersParser.parse(jsonElement);
        assertEquals(copyOf(suggestedReviewers), testCase.expected);
    }

    @DataProvider(name = "resources")
    public Iterator<TestCase[]> dataProvider() {
        return ImmutableList.of(
                new TestCase("suggested_reviewers_1.json")
                        .expect(account("1000097", "jane.roe@example.com", "Jane Roe"))
                        .expect(group("4fd581c0657268f2bdcc26699fbf9ddb76e3a279", "Joiner"))
                        .get()
        ).iterator();
    }

    private static final class TestCase {
        String fileName;
        Set<SuggestedReviewer> expected = Sets.newHashSet();

        TestCase(String fileName) {
            this.fileName = fileName;
        }

        TestCase expect(SuggestedReviewer reviewer) {
            expected.add(reviewer);
            return this;
        }

        TestCase[] get() {
            return new TestCase[]{this};
        }
    }

    private JsonElement getJsonElement(String fileName) throws Exception {
        InputStream stream = getClass().getResourceAsStream(fileName);
        InputStreamReader reader = new InputStreamReader(stream);
        return new JsonParser().parse(reader);
    }

    private AccountInfo account(String id, String email, String name) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(id);
        accountInfo.setEmail(email);
        accountInfo.setName(name);
        return accountInfo;
    }

    private GroupInfo group(String id, String name) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setId(id);
        groupInfo.setName(name);
        return groupInfo;
    }
}
