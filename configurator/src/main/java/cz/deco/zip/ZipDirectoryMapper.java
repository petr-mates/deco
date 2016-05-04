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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZipDirectoryMapper implements Iterable<Map.Entry<String, FileEntry>> {

    private Map<String, FileEntry> zipFileMap = new LinkedHashMap<>();
    private Map<String, String> fileZipMap = new HashMap<>();

    public void put(Path zipEntry, Path filePath, EntryType type) {
        zipFileMap.put(zipEntry.toString(), new FileEntryImpl(filePath.toString(), type));
        fileZipMap.put(filePath.toString(), zipEntry.toString());
    }

    public void putAll(Path zipEntry, ZipDirectoryMapper mapper) {
        for (Map.Entry<String, FileEntry> stringFileEntryEntry : mapper.zipFileMap.entrySet()) {
            String newZipEntry = zipEntry.toString() + stringFileEntryEntry.getKey();
            String newFileName = stringFileEntryEntry.getValue().getFilePath();
            zipFileMap.put(newZipEntry, new FileEntryImpl(newFileName, stringFileEntryEntry.getValue().getEntryType()));
            fileZipMap.put(newFileName, newZipEntry);
        }
    }

    public String getOriginName(Path fileName) {
        String name = fileZipMap.get(fileName.toString());
        if (name == null) {
            return null;
        }
        return Paths.get(name).getFileName().toString();
    }

    public EntryType getOriginType(Path fileName) {
        String key = fileZipMap.get(fileName.toString());
        FileEntry fileEntry = zipFileMap.get(key);
        if (fileEntry == null) {
            return null;
        }
        return fileEntry.getEntryType();
    }

    @Override
    public Iterator<Map.Entry<String, FileEntry>> iterator() {
        return Collections.unmodifiableMap(zipFileMap).entrySet().iterator();
    }

    private class FileEntryImpl implements FileEntry {

        private String filePath;

        private EntryType entryType;

        private FileEntryImpl(String filePath, EntryType entryType) {
            this.filePath = filePath;
            this.entryType = entryType;
        }

        @Override
        public EntryType getEntryType() {
            return entryType;
        }

        @Override
        public String getFilePath() {
            return filePath;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        for (Map.Entry<String, FileEntry> stringFileEntryEntry : zipFileMap.entrySet()) {
            sb.append('(');
            sb.append("zip: ");
            sb.append(stringFileEntryEntry.getKey());
            sb.append(" value: ");
            sb.append(stringFileEntryEntry.getValue().getFilePath());
            sb.append(" type: ");
            sb.append(stringFileEntryEntry.getValue().getEntryType());
            sb.append(")\n");
        }
        return sb.toString();
    }
}
