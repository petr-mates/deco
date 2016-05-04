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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.List;

/**
 * Class do all stuff with xml Nodes and try to resolve all EL expressions.
 */
public class ELReplacer {

    private static final Logger LOG = LoggerFactory.getLogger(ELReplacer.class);

    private VariableContext context;

    public ELReplacer(List<VariableDefinition> variables) {
        context = new VariableContext(variables);
    }

    /**
     * walk through Node list and replace all expressions.
     *
     * @param nodes
     */
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

    /**
     * just evaluate expression.
     *
     * @param source
     * @return
     */
    protected String replaceText(String source) {
        ExpressionFactory factory = ELFactory.getFactory();
        ValueExpression ve = factory.createValueExpression(context, source, String.class);
        String replaced = (String) ve.getValue(context);
        LOG.debug("string {} is replaced to {}", source, replaced);
        return replaced;
    }
}
