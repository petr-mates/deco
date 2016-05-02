package cz.deco;

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

import cz.deco.core.DecoContextImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ApplicationIT {

    private DecoContextImpl context = new DecoContextImpl();

    private String deplPlanName = "deployment-plan.xml";
    private String sourceApplicationArchive = "application.ear-test";

    private String sourceDir = new File("src/test/resources/projects/").getAbsolutePath();
    private static File targetDir = new File("target/it/");

    private ZipTester zipTester = new ZipTester();

    @BeforeClass
    public static void staticInit() throws IOException {
        FileUtils.deleteDirectory(targetDir);
        targetDir.mkdirs();
    }

    @Before
    public void init() throws IOException {
        context = new DecoContextImpl();
    }

    protected void initForProject(String projectName) {
        context.setDeploymentPlan(Paths.get(sourceDir, projectName, deplPlanName));
        context.setOutputArchive(Paths.get(targetDir.getAbsolutePath(), projectName, "out.ear"));
        context.setTemporaryDir(Paths.get(targetDir.getAbsolutePath(), projectName, "tmp"));
        context.setApplicationArchive(Paths.get(sourceDir, projectName, sourceApplicationArchive));
    }

    protected List<String> getOutPath(String project) {
        try {
            return zipTester.getEntries(Paths.get(new File(targetDir, project + "/out.ear").getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testProject01() {
        initForProject("project-01");
        new Application().doWork(context);
        List<String> out = getOutPath("project-01");
        Assert.assertTrue(out.contains("/META-INF/application.xml"));
        Assert.assertTrue(out.contains("/META-INF/MANIFEST.MF"));
        Assert.assertEquals(2, out.size());
    }

    @Test
    public void testProject02() {
        initForProject("project-02");
        new Application().doWork(context);
        List<String> out = getOutPath("project-02");
        Assert.assertTrue(out.contains("/META-INF/application.xml"));
        Assert.assertTrue(out.contains("/META-INF/MANIFEST.MF"));
        Assert.assertEquals(2, out.size());
    }

    @Test
    public void testProject03() {
        initForProject("project-03");
        new Application().doWork(context);
        List<String> out = getOutPath("project-03");
        Assert.assertTrue(out.contains("/META-INF/application.xml"));
        Assert.assertTrue(out.contains("/META-INF/MANIFEST.MF"));
        Assert.assertEquals(2, out.size());
    }

    @Test
    public void testProject04() {
        initForProject("project-04");
        new Application().doWork(context);
        List<String> out = getOutPath("project-04");
        Assert.assertTrue(out.contains("/META-INF/application.xml"));
        Assert.assertTrue(out.contains("/META-INF/MANIFEST.MF"));
        Assert.assertTrue(out.contains("/ejb.jar"));
        Assert.assertEquals(3, out.size());
    }
}
