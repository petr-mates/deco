package cz.deco.deployment;

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

import cz.deco.core.DecoException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * deployment plan value object.
 */
public class DeploymentPlanFile {
    private URI file;

    /**
     * default constor with URI. URI is basic file locator.
     *
     * @param file
     */
    public DeploymentPlanFile(URI file) {
        this.file = file;
    }

    protected URI getFile() {
        return file;
    }

    /**
     * construct Input Stream for deployment plan
     *
     * @return new InputStream
     * @throws FileNotFoundException
     */
    public InputStream getInputStream() {
        try {
            return file.toURL().openStream();
        } catch (IOException e) {
            throw new DecoException("cannot open stream for " + file, e);
        }
    }
}
