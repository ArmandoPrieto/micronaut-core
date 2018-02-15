/*
 * Copyright 2018 original authors
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
package org.particleframework.configuration.mongo.reactive;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.particleframework.context.annotation.Bean;
import org.particleframework.context.annotation.Factory;
import org.particleframework.context.annotation.Primary;
import org.particleframework.context.annotation.Requires;
import org.particleframework.runtime.context.scope.Refreshable;

/**
 * Factory for the default {@link MongoClient}. Creates the injectable {@link Primary} bean
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Requires(beans = MongoConfiguration.class)
@Factory
public class DefaultMongoClientFactory {

    @Bean(preDestroy = "close")
    @Refreshable
    @Primary
    MongoClient mongoClient(MongoConfiguration mongoConfiguration) {
        return MongoClients.create(mongoConfiguration.buildSettings());
    }
}
