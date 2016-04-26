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

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.MapELResolver;
import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the ELResolver
 */
public class VariableResolver extends ELResolver {

    private Map<String, String> variables = new HashMap<>();

    private MapELResolver delegate = new MapELResolver(false);

    public VariableResolver(List<VariableDefinition> variable) {
        importVariables(variable);
    }

    protected void importVariables(List<VariableDefinition> variable) {
        for (VariableDefinition variableDefinition : variable) {
            String name = variableDefinition.getName();
            String value = variableDefinition.getValue();
            if (name != null) {
                variables.put(name, value);
            }
        }
    }

    @Override
    public Object getValue(ELContext elContext, Object base, Object property) {
        Object currentBase = base;
        if (currentBase == null) {
            currentBase = variables;
        }
        return delegate.getValue(elContext, currentBase, property);
    }

    @Override
    public Class<?> getType(ELContext elContext, Object base, Object property) {
        return delegate.getType(elContext, base, property);
    }

    @Override
    public void setValue(ELContext elContext, Object o, Object property, Object value) {
    }

    @Override
    public boolean isReadOnly(ELContext elContext, Object base, Object property) {
        return delegate.isReadOnly(elContext, base, property);
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
        return delegate.getFeatureDescriptors(elContext, base);
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
        return delegate.getCommonPropertyType(elContext, base);
    }
}
