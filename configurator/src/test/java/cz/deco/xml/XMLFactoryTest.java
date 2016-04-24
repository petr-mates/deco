package cz.deco.xml;

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

public class XMLFactoryTest {

    @Test
    public void getXPathFactory() throws Exception {
        Assert.assertNotNull(XMLFactory.newInstance().getXPathFactory());
    }

    @Test
    public void newInstance() throws Exception {
        Assert.assertNotNull(XMLFactory.newInstance());
    }

    @Test
    public void getBuilderNs() throws Exception {
        Assert.assertNotNull(XMLFactory.newInstance().getBuilderNs());
    }

    @Test
    public void getContext() throws Exception {
        Assert.assertNotNull(XMLFactory.newInstance().getContext());
    }

    @Test
    public void getSchemaStream() throws Exception {
        Assert.assertNotNull(XMLFactory.newInstance().getBuilderNs());
    }
}