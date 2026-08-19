/*
 * Copyright 2009-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.eclipse.test.adapters

import static org.eclipse.jdt.groovy.core.tests.GroovyBundle.isRecoveryParser
import static org.junit.Assume.assumeTrue

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.eclipse.test.GroovyEclipseTestSuite
import org.eclipse.core.runtime.Adapters
import org.junit.Test

final class GroovyFileAdapterFactoryTests extends GroovyEclipseTestSuite {

    @Test
    void testFileAdapter() {
        def unit = addGroovySource('class MainClass { static void main(String[] args){}}', 'MainClass', 'pack1')
        buildProject()

        ClassNode node = Adapters.adapt(unit.resource, ClassNode)
        assert node.name == 'pack1.MainClass'
        assert !node.getMethods('main').empty
        assert !node.isInterface()
    }

    @Test
    void testFileAdapterCompileError() {
        assumeTrue(isRecoveryParser())

        def unit = addGroovySource('class OtherClass { static void main(String[] args', 'OtherClass', 'pack1')
        buildProject()

        ClassNode node = Adapters.adapt(unit.resource, ClassNode)
        assert node.name == 'pack1.OtherClass'
        assert node.getMethods('main').empty
        assert !node.isInterface()
    }

    @Test
    void testFileAdapterCompileError2() {
        assumeTrue(isRecoveryParser())

        def unit = addGroovySource('class C { abstract def foo() {} }', 'C', 'pack1')
        buildProject()

        ClassNode node = Adapters.adapt(unit.resource, ClassNode)
        assert node == null
    }

    @Test
    void testFileAdapterNotGroovyFile() {
        def notScript = addPlainText('this is not a groovy file', 'NotGroovy.file')
        buildProject()

        ClassNode node = Adapters.adapt(notScript, ClassNode)
        assert node == null
    }
}
