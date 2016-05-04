# Deco maven plugin

You can use deco maven plugin to configured your application.
deco-maven-plugin is configure to run in verify phase.

maven configuration for deco plugin.

~~~xml
    <build>
        <plugins>
...
            <plugin>
                <groupId>cz.deco.javaee.depl</groupId>
                <artifactId>deco-maven-plugin</artifactId>
                <version>${version.deco}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>configure</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <deploymentPlan>src/main/javaee/deployment-plan.xml</deploymentPlan>
                </configuration>
            </plugin>
            ...
        </plugins>
    </build>
~~~

After run "mvn verify", new artifact "finalName"-deco."packaging" will occur in build directory.
