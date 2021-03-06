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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * deco configure. Take archive and deployment-plan and generate new archive
 * with configured deployment descriptors.
 */
@Mojo(name = "configure", defaultPhase = LifecyclePhase.VERIFY)
public class ConfigureMojo extends AbstractMojo {

    /**
     * application archive thean sould be configured. If not specified, then current artifact is taken.
     */
    @Parameter(property = "sourceFile", required = false)
    private File sourceFile;

    /**
     * target archive, that will be created.
     */
    @Parameter(property = "targetFile", required = false,
            defaultValue = "${project.build.directory}/${project.build.finalName}-deco.${project.packaging}")
    private File targetFile;

    /**
     * deployment plan source file.
     */
    @Parameter(property = "deploymentPlan", required = true)
    private File deploymentPlan;

    /**
     * temporary directory.
     */
    @Parameter(property = "tempDir", required = true,
            defaultValue = "${project.build.directory}/deco/tmp")
    private File tempDir;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (sourceFile == null) {
            sourceFile = project.getArtifact().getFile();
        }

        DecoContextImpl decoContext = new DecoContextImpl();
        decoContext.setDeploymentPlan(deploymentPlan.getAbsoluteFile().toPath());
        decoContext.setApplicationArchive(sourceFile.getAbsoluteFile().toPath());
        decoContext.setTemporaryDir(tempDir.getAbsoluteFile().toPath());
        decoContext.setOutputArchive(targetFile.getAbsoluteFile().toPath());
        new Application().doWork(decoContext);

    }
}
