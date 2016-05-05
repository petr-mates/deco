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
import cz.deco.path.PathUtils;
import cz.deco.xml.XMLFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
    private DocumentBuilder builderNs;
    private XPath xpath = XMLFactory.newInstance().getXPathFactory().newXPath();

    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);

        try {
            builderNs = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void staticInit() throws IOException {
        new PathUtils().deleteDirectory(targetDir.getAbsoluteFile().toPath());
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
        String absolutePath = new File(targetDir, "project-02/tmp/META-INF/application.xml").getAbsolutePath();
        test(absolutePath,
                "/application/env-entry/entry-name/text()", "name");
        test(absolutePath,
                "/application/env-entry/entry-value/text()", "value");
    }

    @Test
    public void testProject03() {
        initForProject("project-03");
        new Application().doWork(context);
        List<String> out = getOutPath("project-03");
        Assert.assertTrue(out.contains("/META-INF/application.xml"));
        Assert.assertTrue(out.contains("/META-INF/MANIFEST.MF"));
        Assert.assertEquals(2, out.size());
        String absolutePath = new File(targetDir, "project-03/tmp/META-INF/application.xml").getAbsolutePath();
        test(absolutePath,
                "/application/libDirectory/text()", "lib");
        test(absolutePath,
                "/application/module[1]//ejb/text()", "ejb0");
        test(absolutePath,
                "/application/module[2]//ejb/text()", "ejb");
        test(absolutePath,
                "/application/module[3]//ejb/text()", "ejb2");
        test(absolutePath,
                "/application/env-entry/entry-name/text()", "xxx");
        test(absolutePath,
                "/application/env-entry/entry-value/text()", "xvalue");
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

    protected void test(String source, String xpathExpression, String value) {
        try {
            Document parse = builderNs.parse(new File(source));
            Assert.assertEquals(value, xpath.evaluate(xpathExpression,
                    parse.getDocumentElement(), XPathConstants.STRING));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
