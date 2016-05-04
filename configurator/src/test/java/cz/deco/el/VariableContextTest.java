package cz.deco.el;

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

import cz.deco.javaee.deployment_plan.VariableDefinition;
import org.junit.Assert;
import org.junit.Test;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.ArrayList;
import java.util.List;

public class VariableContextTest {

    @Test
    public void testElSimple() {
        List<VariableDefinition> objects = new ArrayList<>();
        VariableDefinition variableDefinition = new VariableDefinition();
        variableDefinition.setName("name");
        variableDefinition.setValue("value");
        objects.add(variableDefinition);

        VariableContext variableContext = new VariableContext(objects);

        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        ValueExpression ve = expressionFactory.createValueExpression(variableContext, "${name}", Object.class);
        Object result = ve.getValue(variableContext);
        Assert.assertEquals("value", result);
    }

    @Test
    public void testElSimpleEmptyExpression() {
        List<VariableDefinition> objects = new ArrayList<>();
        VariableDefinition variableDefinition = new VariableDefinition();
        variableDefinition.setName("name");
        variableDefinition.setValue("value");
        objects.add(variableDefinition);

        VariableContext variableContext = new VariableContext(objects);

        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        ValueExpression ve = expressionFactory.createValueExpression(variableContext, "", String.class);
        Object result = ve.getValue(variableContext);
        Assert.assertEquals("", result);
    }

    @Test
    public void testElAdd() {
        List<VariableDefinition> objects = new ArrayList<>();
        VariableDefinition variableDefinition = new VariableDefinition();
        variableDefinition.setName("x");
        variableDefinition.setValue("10");
        objects.add(variableDefinition);

        variableDefinition = new VariableDefinition();
        variableDefinition.setName("y");
        variableDefinition.setValue("20");
        objects.add(variableDefinition);

        VariableContext variableContext = new VariableContext(objects);

        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        ValueExpression ve = expressionFactory.createValueExpression(variableContext, "${x+y}", String.class);
        Object result = ve.getValue(variableContext);
        Assert.assertEquals("30", result);
    }

}
