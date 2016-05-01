package cz.deco.descriptor;

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

import cz.deco.core.DecoException;
import cz.deco.xml.XMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class DescriptorHolder {

    private static final Logger LOG = LoggerFactory.getLogger(DescriptorHolder.class);

    private Descriptor descriptor;
    private XMLFactory xmlFactory = XMLFactory.newInstance();

    public Document getDocument() {
        return descriptor.getDocument();
    }

    protected void setDescriptor(Descriptor descriptorToStore) {
        descriptor = descriptorToStore;
    }

    protected Path getPath() {
        return descriptor.getPath();
    }

    public boolean load(Path path) {
        try {
            Document loadedDocument = xmlFactory.getBuilderNs().parse(path.toFile());
            setDescriptor(new DescriptorImpl(path, loadedDocument));
            return true;
        } catch (SAXException e) {
            LOG.info("{} not parsed {}", path, e, null);
        } catch (IOException e) {
            LOG.info("{} not loaded {}", path, e, null);
        }
        return false;
    }

    public void storeCurrent() {
        Transformer transformer = xmlFactory.getTransformerFactory();
        try (FileWriter out = new FileWriter(getPath().toFile())) {
            StreamResult result = new StreamResult(out);
            DOMSource source = new DOMSource(getDocument());
            transformer.transform(source, result);
        } catch (IOException | TransformerException e) {
            throw new DecoException("cannot store descriptor", e);
        }
    }
}
