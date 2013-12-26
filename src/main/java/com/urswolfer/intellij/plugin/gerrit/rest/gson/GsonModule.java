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

package com.urswolfer.intellij.plugin.gerrit.rest.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;

import java.util.Date;

/**
 * @author Thomas Forrer
 */
public class GsonModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Gson.class).toProvider(new Provider<Gson>() {
            @Override
            public Gson get() {
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(Date.class, new DateDeserializer());
                builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                return builder.create();
            }
        }).asEagerSingleton();
    }
}
