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

import cz.deco.core.DecoException;
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

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTest {

    @Mock
    private PackApplication pack;

    @Mock
    Path path;

    @Mock
    private UnpackApplication unpack;

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
}
