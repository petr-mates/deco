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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Path;

@RunWith(MockitoJUnitRunner.class)
public class DecoContextImplTest {

    @Mock
    private Path path;

    @Test
    public void getApplicationArchive() throws Exception {
        DecoContextImpl decoContext = new DecoContextImpl();
        Assert.assertNull(decoContext.getApplicationArchive());
        decoContext.setApplicationArchive(path);
        Assert.assertSame(path, decoContext.getApplicationArchive());
    }

    @Test
    public void getDeploymentPlan() throws Exception {
        DecoContextImpl decoContext = new DecoContextImpl();
        Assert.assertNull(decoContext.getDeploymentPlan());
        decoContext.setDeploymentPlan(path);
        Assert.assertSame(path, decoContext.getDeploymentPlan());
    }

    @Test
    public void getTemporaryDir() throws Exception {
        DecoContextImpl decoContext = new DecoContextImpl();
        Assert.assertNull(decoContext.getTemporaryDir());
        decoContext.setTemporaryDir(path);
        Assert.assertSame(path, decoContext.getTemporaryDir());
    }

    @Test
    public void getOutputArchive() throws Exception {
        DecoContextImpl decoContext = new DecoContextImpl();
        Assert.assertNull(decoContext.getOutputArchive());
        decoContext.setOutputArchive(path);
        Assert.assertSame(path, decoContext.getOutputArchive());
    }
}