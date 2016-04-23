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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.List;

public class ELReplacer {

    private VariableContext context;

    public ELReplacer(List<VariableDefinition> variables) {
        context = new VariableContext(variables);
    }

    public void replaceText(NodeList nodes) {
        int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            Node actual = nodes.item(i);
            NodeList childNodes = actual.getChildNodes();
            if (childNodes != null) {
                replaceText(childNodes);
            }
            String nodeValue = actual.getNodeValue();
            if (nodeValue != null) {
                String replacedValue = replaceText(nodeValue);
                actual.setNodeValue(replacedValue);
            }
        }
    }

    protected String replaceText(String s) {
        ExpressionFactory factory = ELFactory.getFactory();
        ValueExpression ve = factory.createValueExpression(context, s, String.class);
        return (String) ve.getValue(context);
    }
}
