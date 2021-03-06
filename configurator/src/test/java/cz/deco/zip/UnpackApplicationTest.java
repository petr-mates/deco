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

import cz.deco.path.PathUtils;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class UnpackApplicationTest {

    private File targetDir;

    @Before
    public void init() throws IOException {
        targetDir = new File("target/test-data.zip/out/");
        new PathUtils().deleteDirectory(targetDir.getAbsoluteFile().toPath());
        targetDir.mkdirs();
    }

    @Test
    public void unpackAndPack() throws Exception {
        UnpackApplication unpackApplication = new UnpackApplication();
        Path pathToApplication = new File("src/test/resources/zip-data/test-data.zip").toPath();

        ZipDirectoryMapper mapper = unpackApplication.unpackZip(pathToApplication, Paths.get(targetDir.toURI()));
        String resultString = mapper.toString();
        Assert.assertThat(resultString, StringContains.containsString("zip: /inner.zip/schemas/"));
        Assert.assertThat(resultString, StringContains.containsString("zip: /inner.zip/schemas/target/"));
        Assert.assertThat(resultString, StringContains.containsString("zip: /inner.zip/schemas/target/schemas-0.0.1-SNAPSHOT.jar/deployment-plan.xsd"));
        Assert.assertThat(resultString, StringContains.containsString("/inner.zip/schemas/target/schemas-0.0.1-SNAPSHOT.jar"));
        Assert.assertThat(resultString, StringContains.containsString("/configurator/target/configurator-0.0.1-SNAPSHOT.jar/META-INF/"));
        Assert.assertThat(resultString, StringContains.containsString("/inner.zip"));
        Assert.assertThat(resultString, StringContains.containsString("/configurator/pom.xml"));

        pack(mapper);
    }

    public void pack(ZipDirectoryMapper mapper) throws Exception {
        Path path = Paths.get(targetDir.toURI());
        PackApplication packApplication = new PackApplication(mapper);
        packApplication.createZip(path, new File("target/test-data.zip/out.zip").getAbsoluteFile().toPath());
        final Map<String, String> env = Collections.singletonMap("create", "true");
    }
}
