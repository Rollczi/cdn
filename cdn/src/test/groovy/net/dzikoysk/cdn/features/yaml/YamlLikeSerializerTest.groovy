/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.features.yaml

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.TestConfiguration
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class YamlLikeSerializerTest extends CdnSpec {

    @Test
    void 'should serialize entity with indentation based formatting' () {
        assertEquals cfg("""
        # Root entry description
        rootEntry: default value

        // Section description
        section:
          # Random value
          subEntry: -1
          # List description
          list:
            - record
            - record : with : semicolons
          # Custom object
          custom:
            id: rtx
            count: 3070
        """), yamlLike.render(new TestConfiguration())
    }

}
