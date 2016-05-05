package cz.deco.core;

/*
 * #%L
 * Descriptor Configurator
 * %%
 * Copyright (C) 2016 Mates
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.nio.file.Path;

/**
 * deco context holds application data from start application.
 */
public interface DecoContext {
    /**
     * Path to application archive.
     *
     * @return
     */
    Path getApplicationArchive();

    /**
     * Path to deployment plan.
     *
     * @return
     */
    Path getDeploymentPlan();

    /**
     * temporary directory.
     *
     * @return
     */
    Path getTemporaryDir();

    /**
     * Path to target application archive.
     *
     * @return
     */
    Path getOutputArchive();
}
