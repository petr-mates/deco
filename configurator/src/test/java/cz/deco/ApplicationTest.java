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
import cz.deco.core.DecoException;
import cz.deco.deployment.DeploymentPlanFile;
import cz.deco.deployment.DeploymentPlanLoader;
import cz.deco.el.PlanELResolver;
import cz.deco.javaee.deployment_plan.DeploymentPlan;
import cz.deco.zip.PackApplication;
import cz.deco.zip.UnpackApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTest {

    @Mock
    private PackApplication pack;

    @Mock
    private Path path;

    @Mock
    private UnpackApplication unpack;

    @Mock
    private DeploymentPlanLoader planLoader;

    @Mock
    private PlanELResolver planELResolver;

    @InjectMocks
    private Application application;

    @Test
    public void packApplicationArchive() throws Exception {
        Application application = new Application();
        application.packApplicationArchive(path, path, this.pack);
        Mockito.verify(pack, Mockito.times(1)).createZip(path, path);
    }

    @Test(expected = DecoException.class)
    public void packApplicationArchiveError() throws Exception {
        Application application = new Application();
        Mockito.when(pack.createZip(path, path)).thenThrow(new IOException("some io exception"));
        application.packApplicationArchive(path, path, this.pack);
        Mockito.verify(pack, Mockito.times(1)).createZip(path, path);
    }

    @Test
    public void unpack() throws Exception {
        application.unpack(path, path);
    }

    @Test(expected = DecoException.class)
    public void unpackException() throws Exception {
        Mockito.when(unpack.unpackZip(path, path)).thenThrow(new IOException());
        application.unpack(path, path);
    }

    @Test(expected = DecoException.class)
    public void testCheckArchive() {
        application.checkArchive(Paths.get("test/x"));
    }


    @InjectMocks
    Application applicationDoWork = new Application() {
        @Override
        protected void checkArchive(Path applicationArchive) {
        }

        @Override
        protected void packApplicationArchive(Path tempDir, Path outputZip, PackApplication pack) {
        }
    };

    @Test
    public void testDoWork() {
        DecoContextImpl decoContext = new DecoContextImpl();
        Mockito.when(planLoader.load((DeploymentPlanFile) Mockito.any())).thenReturn(Mockito.mock(DeploymentPlan.class));

        decoContext.setDeploymentPlan(path);
        applicationDoWork.doWork(decoContext);
    }
}
