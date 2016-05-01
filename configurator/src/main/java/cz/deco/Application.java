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

import cz.deco.core.DecoContext;
import cz.deco.core.DecoException;
import cz.deco.deployment.DeploymentPlanFile;
import cz.deco.deployment.DeploymentPlanLoader;
import cz.deco.descriptor.DescriptorHolder;
import cz.deco.el.PlanELResolver;
import cz.deco.javaee.deployment_plan.DeploymentPlan;
import cz.deco.javaee.deployment_plan.DescriptorOverride;
import cz.deco.path.DecoPathMatcher;
import cz.deco.replacer.Replacer;
import cz.deco.zip.FileEntry;
import cz.deco.zip.PackApplication;
import cz.deco.zip.UnpackApplication;
import cz.deco.zip.ZipDirectoryMapper;
import cz.deco.zip.ZipUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Application {

    private DeploymentPlanLoader planLoader = new DeploymentPlanLoader();
    private UnpackApplication unpack = new UnpackApplication();
    private PlanELResolver planELResolver = new PlanELResolver();
    private Replacer documentReplacer = new Replacer();


    public void doWork(DecoContext context) {
        Path applicationArchive = context.getApplicationArchive();
        Path tempDir = context.getTemporaryDir();
        Path outputArchive = context.getOutputArchive();
        checkArchive(applicationArchive);
        DeploymentPlan plan = planLoader.load(new DeploymentPlanFile(context.getDeploymentPlan().toUri()));
        planELResolver.resolveAllExpressions(plan);

        ZipDirectoryMapper mapper = unpack(applicationArchive, tempDir);
        DecoPathMatcher pathMatcher = new DecoPathMatcher(mapper);

        List<DescriptorOverride> descriptors = plan.getDescriptorOverride();
        for (DescriptorOverride descriptor : descriptors) {
            String path = descriptor.getPath();
            List<FileEntry> matchedDescriptorPaths = pathMatcher.findByPath(path);
            for (FileEntry fileEntry : matchedDescriptorPaths) {
                DescriptorHolder descHolder = new DescriptorHolder();
                descHolder.load(Paths.get(fileEntry.getFilePath()));
                documentReplacer.apply(descHolder.getDocument(), descriptor);
                descHolder.storeCurrent();
            }
        }
        PackApplication pack = new PackApplication(mapper);
        packApplicationArchive(tempDir, outputArchive, pack);
    }

    protected void packApplicationArchive(Path tempDir, Path outputZip, PackApplication pack) {
        try {
            pack.createZip(tempDir, outputZip);
        } catch (IOException e) {
            throw new DecoException("pack application archive error", e);
        }
    }

    protected ZipDirectoryMapper unpack(Path applicationArchive, Path tempDir) {
        try {
            return unpack.unpackZip(applicationArchive, tempDir);
        } catch (IOException e) {
            throw new DecoException("unpack application archive error", e);
        }
    }

    protected void checkArchive(Path applicationArchive) {
        if (!ZipUtils.isZip(applicationArchive)) {
            throw new DecoException("applicatin is no ZIP archve");
        }
    }
}
