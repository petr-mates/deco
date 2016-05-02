# Deployment Descriptor Configurator

Application to configure Java EE applications using deployment plan.


Using deployment plan (xml file) you can change Java EE deployment descriptors. For example
web.xml, application.xml ejb-jar.xml etc.


Deployemnt plan contains variables and xpath.
Variables are added or replaced in Java EE deployment descriptors using defined xpaths.

## How this should works

We have a war.
war structure.

~~~
application.war
    META-INF
        MANIFEST.MF
    WEB-INF
        web.xml
        weblogic.xml
        classes
            ....
~~~
and we have a deployment-plan.xml
~~~xml
<deployment-plan xmlns="http://javaee.deco.cz/deployment-plan">
    <application-name>application</application-name>
    <variable-definitions>
        <variable>
            <description>description</description>
            <name>variable_name</name>
            <value>variable_value</value>
        </variable>
    </variable-definitions>
    <descriptor-override>
        <path>**/web.xml</path>
        <module-descriptor>
            <insert type="insertAsFirstChildOf">
                <xpath>/e:web-app</xpath>
                <value xmlns:e="http://java.sun.com/xml/ns/javaee">
                    <e:env-enttry>
                        <e:env-name>entry_name</e:env-name>
                        <e:env-value>${variable_name}</e:env-value>
                    </e:env-enttry>
                </value>
            </insert>
        </module-descriptor>
    </module-override>
</deployment-plan>
~~~

After run DECO web.xml in application.war/WEB-INF
should be changed and entry_name should be in web.xml with "variable_value".