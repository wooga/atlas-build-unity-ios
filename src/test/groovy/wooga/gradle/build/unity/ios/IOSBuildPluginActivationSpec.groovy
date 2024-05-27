/*
 * Copyright 2018 Wooga GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package wooga.gradle.build.unity.ios

import nebula.test.PluginProjectSpec

class IOSBuildPluginActivationSpec extends PluginProjectSpec {
    def setup() {
        //PluginProjectSpec doesn't create folders for the subprojects it tests, which leads to issues when
        // trying to access provider values _______somehow_______
        def subprojects = ['sub', 'sub1', 'sub2', 'sub3']
        subprojects.forEach { String subProj ->
            new File(projectDir, subProj).mkdirs()
        }
    }
    @Override
    String getPluginName() {
        return 'net.wooga.build-unity-ios'
    }
}
