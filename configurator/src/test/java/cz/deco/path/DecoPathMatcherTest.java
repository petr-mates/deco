package cz.deco.path;

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

import cz.deco.zip.EntryType;
import cz.deco.zip.FileEntry;
import cz.deco.zip.ZipDirectoryMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class DecoPathMatcherTest {


    private ZipDirectoryMapper mapper = new ZipDirectoryMapper();

    @Before
    public void init() {
        mapper.put(Paths.get("conf/test.zip/persistence.xml"),
                Paths.get("conf/test.zip/persistence.xml"), EntryType.FILE);
        mapper.put(Paths.get("/test/test.zip/persistence.xml"),
                Paths.get("test/test.zip/persistence.xml"), EntryType.FILE);

    }

    @Test
    public void findByPathAll() throws Exception {
        DecoPathMatcher decoPathMatcher = new DecoPathMatcher(mapper);
        List<FileEntry> byPath = decoPathMatcher.findByPath("**/*.*");
        Assert.assertEquals(2, byPath.size());
    }

    @Test
    public void findByPathNone() throws Exception {
        DecoPathMatcher decoPathMatcher = new DecoPathMatcher(mapper);
        List<FileEntry> byPath = decoPathMatcher.findByPath("persistence.xml");
        Assert.assertEquals(0, byPath.size());
    }

    @Test
    public void findByPathNode() throws Exception {
        DecoPathMatcher decoPathMatcher = new DecoPathMatcher(new ZipDirectoryMapper());
        List<FileEntry> byPath = decoPathMatcher.findByPath("persistence.xml");
        Assert.assertEquals(0, byPath.size());
    }

    @Test
    public void findByPathOne() throws Exception {
        DecoPathMatcher decoPathMatcher = new DecoPathMatcher(mapper);
        List<FileEntry> byPath = decoPathMatcher.findByPath("conf/**/persistence.xml");
        Assert.assertEquals(1, byPath.size());
        String resultPath = new StringBuilder("conf").append(File.separatorChar).append("test.zip").append(File.separator).append("persistence.xml").toString();
        Assert.assertEquals(resultPath, byPath.get(0).getFilePath());
    }
}
