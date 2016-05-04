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

import cz.deco.zip.FileEntry;
import cz.deco.zip.ZipDirectoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DecoPathMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(DecoPathMatcher.class);

    private static final String GLOB = "glob:";

    private final ZipDirectoryMapper mapper;

    public DecoPathMatcher(ZipDirectoryMapper mapper) {
        this.mapper = mapper;
    }

    public List<FileEntry> findByPath(String path) {
        LOG.debug("searching for pattern {}", path);
        List<FileEntry> result = new ArrayList<>();
        try (FileSystem fs = FileSystems.getDefault()) {
            PathMatcher pathMatcher = fs.getPathMatcher(GLOB + path);
            for (Map.Entry<String, FileEntry> stringFileEntryEntry : mapper) {
                if (pathMatcher.matches(fs.getPath(stringFileEntryEntry.getKey()))) {
                    LOG.debug("entry found {}", stringFileEntryEntry.getValue());
                    result.add(stringFileEntryEntry.getValue());
                }
            }
        } catch (IOException | UnsupportedOperationException e) {
            LOG.debug("error closing FileSystem {}", e, null);
        }
        return result;
    }
}
