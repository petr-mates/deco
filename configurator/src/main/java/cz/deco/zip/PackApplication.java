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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

public class PackApplication {

    private static final Logger LOG = LoggerFactory.getLogger(PackApplication.class);

    private final ZipDirectoryMapper mapper;

    public PackApplication(ZipDirectoryMapper mapper) {
        this.mapper = mapper;
    }

    public void pack(final Path directory, final FileSystem fs) throws IOException {

        Files.walkFileTree(directory, new PathFileVisitor(fs, directory));
    }

    public Path createZip(Path newZip, String originalName) throws IOException {
        Path path = Paths.get(newZip.getParent().toString(), originalName);
        URI newJarFile = URI.create("jar:file:" + path.toString());
        LOG.debug("new jar file {}", newJarFile);
        try (FileSystem fileSystem = FileSystems.newFileSystem(newJarFile,
                Collections.singletonMap("create", "true"))) {
            pack(newZip, fileSystem);
        }
        return path;
    }

    private class PathFileVisitor implements FileVisitor<Path> {
        private final FileSystem fs;
        private final Path directory;
        private int dirCount;

        private PathFileVisitor(FileSystem fs, Path directory) {
            this.fs = fs;
            this.directory = directory;
            dirCount = 0;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (dirCount == 0) {
                dirCount++;
                return FileVisitResult.CONTINUE;
            }

            LOG.debug("directory {}", dir);

            if (mapper.getOriginType(dir) == EntryType.ZIP) {
                Path newZip = createZip(dir, mapper.getOriginName(dir));
                visitFile(newZip, null);
                return FileVisitResult.SKIP_SUBTREE;
            }
            Path relativeDirPath = fs.getPath(directory.relativize(dir).toString());
            Files.createDirectories(relativeDirPath);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Path relativeFilePath = fs.getPath(directory.relativize(file).toString());
            LOG.debug("file {} {}", directory, file);

            Files.copy(file, relativeFilePath, StandardCopyOption.REPLACE_EXISTING);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
