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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZipDirectoryMapper {

    private Map<String, FileEntry> zipFileMap = new LinkedHashMap<>();
    private Map<String, String> fileZipMap = new HashMap<>();

    public void put(Path zipEntry, Path filePath, EntryType type) {
        zipFileMap.put(zipEntry.toString(), new FileEntry(filePath.toString(), type));
        fileZipMap.put(filePath.toString(), zipEntry.toString());
    }

    public void putAll(String zipEntry, ZipDirectoryMapper mapper) {
        for (Map.Entry<String, FileEntry> stringFileEntryEntry : mapper.zipFileMap.entrySet()) {
            String newZipEntry = zipEntry + stringFileEntryEntry.getKey();
            String newFileName = stringFileEntryEntry.getValue().getFileName();
            zipFileMap.put(newZipEntry, new FileEntry(newFileName, stringFileEntryEntry.getValue().getEntryType()));
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

    private class FileEntry {

        private String fileName;

        private EntryType entryType;

        private FileEntry(String fileName, EntryType entryType) {
            this.fileName = fileName;
            this.entryType = entryType;
        }

        public EntryType getEntryType() {
            return entryType;
        }

        public String getFileName() {
            return fileName;
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
            sb.append(stringFileEntryEntry.getValue().getFileName());
            sb.append(" type: ");
            sb.append(stringFileEntryEntry.getValue().getEntryType());
            sb.append(")\n");
        }
        return sb.toString();
    }
}
