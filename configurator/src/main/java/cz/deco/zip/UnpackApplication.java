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
import java.math.BigInteger;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;
import java.util.zip.ZipInputStream;

public class UnpackApplication {

    private static final Random RANDOM = new Random();

    private static final Logger LOG = LoggerFactory.getLogger(UnpackApplication.class);

    public ZipDirectoryMapper unpackZip(Path zipFile, Path pathToTarget) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(zipFile, null)) {
            return unpack(fs.getRootDirectories().iterator().next(), pathToTarget.toString());
        }
    }

    public ZipDirectoryMapper unpack(final Path pathToApplication, final String target) throws IOException {

        final ZipDirectoryMapper mapper = new ZipDirectoryMapper();

        Files.walkFileTree(pathToApplication, new PathFileVisitor(mapper, target));
        return mapper;

    }

    protected Path unpackInnerZip(Path source, Path innerZip, ZipDirectoryMapper currentMapper) throws IOException {
        LOG.debug("unpack zip {} ", innerZip);
        String postfix = getPostFix();
        try (FileSystem fs = FileSystems.newFileSystem(innerZip, null)) {
            Path rootDirectory = fs.getRootDirectories().iterator().next();
            Path newZipDirectory = Paths.get(innerZip.toString() + postfix);
            Files.createDirectories(newZipDirectory);
            ZipDirectoryMapper mapper = unpack(rootDirectory, newZipDirectory.toString());
            currentMapper.putAll(source, mapper);
            return newZipDirectory;
        } finally {
            Files.delete(innerZip);
        }
    }

    protected String getPostFix() {
        byte[] bytes = new byte[5];
        RANDOM.nextBytes(bytes);
        return "." + new BigInteger(bytes).abs().toString(16);
    }

    protected boolean isZip(Path maybeZip) {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(maybeZip))) {
            return zipInputStream.getNextEntry() != null;
        } catch (IOException e) {
            LOG.error("error opening zip inputstream {}", e, null);
        }
        return false;
    }

    private class PathFileVisitor implements FileVisitor<Path> {
        private final ZipDirectoryMapper mapper;
        private final String target;
        private int directoryCount;

        private PathFileVisitor(ZipDirectoryMapper mapper, String target) {
            this.mapper = mapper;
            this.target = target;
            directoryCount = 0;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            LOG.debug("create directory {} ", dir);
            Path targetDirPath = resolvePath(dir);
            Files.createDirectories(targetDirPath);
            if (directoryCount != 0) {
                mapper.put(dir, targetDirPath.toAbsolutePath(), EntryType.DIRECTORY);
            }
            directoryCount++;
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            LOG.debug("create file {} ", file);
            Path targetZip = resolvePath(file);
            Files.copy(file, targetZip, StandardCopyOption.REPLACE_EXISTING);
            if (isZip(file)) {
                Path dirPath = unpackInnerZip(file, targetZip, mapper);
                mapper.put(file, dirPath, EntryType.ZIP);
            } else {
                mapper.put(file, targetZip, EntryType.FILE);
            }
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

        protected Path resolvePath(Path last) {
            return Paths.get(target, last.toString());
        }
    }
}
