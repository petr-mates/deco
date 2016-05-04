package cz.deco.zip;

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

import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public class ZipDirectoryMapperTest {

    @Test
    public void testPut() throws Exception {
        ZipDirectoryMapper mapper = new ZipDirectoryMapper();
        Assert.assertFalse(mapper.iterator().hasNext());
        mapper.put(Paths.get("conf"), Paths.get("test"), EntryType.ZIP);
        Assert.assertTrue(mapper.iterator().hasNext());
    }

    @Test
    public void testPutAll() throws Exception {
        ZipDirectoryMapper mapper = new ZipDirectoryMapper();
        mapper.put(Paths.get("/conf/"), Paths.get("test"), EntryType.ZIP);
        ZipDirectoryMapper putAll = new ZipDirectoryMapper();
        putAll.putAll(Paths.get("/conf/"), mapper);
        Assert.assertEquals("/conf/conf", putAll.iterator().next().getKey());
    }

    @Test
    public void getOriginName() throws Exception {
        ZipDirectoryMapper mapper = new ZipDirectoryMapper();
        mapper.put(Paths.get("/conf/name.xml"), Paths.get("test"), EntryType.ZIP);
        Assert.assertEquals("name.xml", mapper.getOriginName(Paths.get("test")));
        Assert.assertEquals(EntryType.ZIP, mapper.getOriginType(Paths.get("test")));
        Assert.assertNull(mapper.getOriginName(Paths.get("testx")));
    }

    @Test
    public void getOriginType() throws Exception {
        ZipDirectoryMapper mapper = new ZipDirectoryMapper();
        mapper.put(Paths.get("/conf/name.xml"), Paths.get("test"), EntryType.ZIP);
        Assert.assertEquals(EntryType.ZIP, mapper.getOriginType(Paths.get("test")));
        Assert.assertNull(mapper.getOriginType(Paths.get("testx")));
    }

    @Test
    public void testToString() throws Exception {
        ZipDirectoryMapper mapper = new ZipDirectoryMapper();
        mapper.put(Paths.get("/conf/name.xml"), Paths.get("test"), EntryType.ZIP);
        Assert.assertThat(mapper.toString(), StringContains.containsString("zip: /conf/name.xml"));
    }
}