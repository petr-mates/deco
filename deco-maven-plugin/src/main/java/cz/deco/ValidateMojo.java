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

import cz.deco.deployment.DeploymentPlanFile;
import cz.deco.deployment.DeploymentPlanLoader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * validate deployment plan. default phase is validate.
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VALIDATE, aggregator = true)
public class ValidateMojo extends AbstractMojo {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateMojo.class);
    /**
     * deployment plan source file.
     */
    @Parameter(property = "deploymentPlan", required = true)
    private File deploymentPlan;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        DeploymentPlanFile deploymentPlanFile = new DeploymentPlanFile(deploymentPlan.toURI());
        new DeploymentPlanLoader().load(deploymentPlanFile);
        LOG.info("deployment plan validation OK");
    }
}
