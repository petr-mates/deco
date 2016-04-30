package cz.deco.descriptor;

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

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@RunWith(MockitoJUnitRunner.class)
public class DescriptorHolderTest {
    private static final Logger LOG = LoggerFactory.getLogger(DescriptorHolderTest.class);

    @Mock
    private Descriptor descriptor;
    @Mock
    private Document document;
    @Mock
    private Path path;

    private DescriptorHolder descriptorHolder = new DescriptorHolder();

    private Path pathToStore = new File("target/test/resources/cz/deco/descriptor/web.xml").toPath();

    @Before
    public void init() {
        descriptorHolder = new DescriptorHolder();
        File parentDirectory = pathToStore.getParent().toFile();
        try {
            FileUtils.deleteDirectory(parentDirectory);
        } catch (IOException e) {
            LOG.error("error delete direcotry {}", pathToStore, e);
        }
        parentDirectory.mkdirs();
    }

    @Test
    public void getDocument() throws Exception {
        Mockito.when(descriptor.getDocument()).thenReturn(document);
        descriptorHolder.setDescriptor(descriptor);
        Assert.assertSame(document, descriptorHolder.getDocument());
    }

    @Test
    public void getPath() throws Exception {
        Mockito.when(descriptor.getPath()).thenReturn(path);
        descriptorHolder.setDescriptor(descriptor);
        Assert.assertSame(path, descriptorHolder.getPath());
    }

    @Test
    public void load() throws Exception {
        Assert.assertTrue(descriptorHolder.load(new File("src/test/resources/cz/deco/descriptor/web.xml").toPath()));
        Assert.assertNotNull(descriptorHolder.getDocument());
        Assert.assertFalse(descriptorHolder.load(new File("src/main/resources/cz/deco/descriptor/web.xml").toPath()));
    }

    @Test
    public void loadInvlaid() throws Exception {
        Assert.assertFalse(descriptorHolder.load(new File("src/test/resources/cz/deco/descriptor/web-invalid.xml").toPath()));
    }

    @Test
    public void storeCurrent() throws Exception {
        Assert.assertFalse(pathToStore.toFile().exists());
        DescriptorHolder descriptorHolder = new DescriptorHolder() {
            @Override
            protected Path getPath() {
                return pathToStore;
            }
        };
        descriptorHolder.load(new File("src/test/resources/cz/deco/descriptor/web.xml").toPath());
        descriptorHolder.storeCurrent();
        Assert.assertTrue(pathToStore.toFile().exists());
    }
}