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
import cz.deco.path.PathUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private Main() {
        super();
    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        DecoContextImpl context = new DecoContextImpl();
        Options options = getOptions();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar deco.jar", options);
            return;
        }

        String source = cmd.getOptionValue("s");
        String target = cmd.getOptionValue("t");
        String plan = cmd.getOptionValue("p");
        String temp = cmd.getOptionValue("d");

        context.setOutputArchive(new File(target).getAbsoluteFile().toPath());
        context.setApplicationArchive(new File(source).getAbsoluteFile().toPath());
        Path temporaryDir;
        if (cmd.hasOption("d")) {
            temporaryDir = new File(temp).getAbsoluteFile().toPath();
        } else {
            temporaryDir = getTempDirectory();
        }
        context.setTemporaryDir(temporaryDir);
        context.setDeploymentPlan(new File(plan).getAbsoluteFile().toPath());

        new Application().doWork(context);

        cleanUp(temporaryDir);

    }

    protected static Options getOptions() {
        Options options = new Options();
        Option source = Option.builder("s")
                .longOpt("source")
                .argName("sourceArchive")
                .hasArg()
                .required().desc("source archive file path")
                .build();
        Option target = Option.builder("t")
                .longOpt("target")
                .argName("targetArchive")
                .hasArg()
                .required().desc("target archive file path")
                .build();
        Option plan = Option.builder("p")
                .longOpt("plan")
                .argName("planFile")
                .hasArg()
                .required().desc("deployment plan file path")
                .build();
        Option temp = Option.builder("d")
                .longOpt("temp")
                .argName("tempDir")
                .hasArg()
                .desc("temporary directory")
                .build();
        options.addOption(plan);
        options.addOption(source);
        options.addOption(target);
        options.addOption(temp);
        return options;
    }

    protected static Path getTempDirectory() {
        try {
            return Files.createTempDirectory("deco");
        } catch (IOException e) {
            throw new DecoException("error creating temp directory", e);
        }
    }

    protected static void cleanUp(Path tmpDir) {
        try {
            new PathUtils().deleteDirectory(tmpDir);
        } catch (IOException e) {
            throw new DecoException("error delete temp directory", e);
        }
    }
}
