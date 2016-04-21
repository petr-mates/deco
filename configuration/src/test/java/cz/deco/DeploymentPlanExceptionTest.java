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

import org.junit.Assert;
import org.junit.Test;

public class DeploymentPlanExceptionTest {
    @Test
    public void testDeploymentPlanExceptionMessage() {
        DeploymentPlanException message = new DeploymentPlanException("message");
        Assert.assertEquals("message", message.getMessage());
    }

    @Test
    public void testDeploymentPlanExceptionMessageCause() {
        Exception ex = new Exception();
        DeploymentPlanException message = new DeploymentPlanException("message", ex);
        Assert.assertEquals("message", message.getMessage());
        Assert.assertSame(ex, message.getCause());
    }
}