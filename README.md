# gattbinder
Bindings generator

Example of use:

	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
		<modelVersion>4.0.0</modelVersion>
		<groupId>cz.gattserver</groupId>
		<artifactId>gattbinder-dummy</artifactId>
		<version>1.0.0-SNAPSHOT</version>
		<name>Gattserver binder dummy</name>
		<url>https://www.gattserver.cz</url>
		<description>Gattserver lib for generating bindings</description>
	 
		<dependencies>
			<dependency>
				<groupId>cz.gattserver</groupId>
				<artifactId>gattbinder</artifactId>
				<version>1.0.0-SNAPSHOT</version>
			</dependency>
		</dependencies>
	 
		<build>
			<plugins>
	 
				<!-- Runs the processor -->
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-compiler-plugin</artifactId>
					<configuration>
						<source>1.8</source>
						<target>1.8</target>
						<encoding>UTF-8</encoding>
						<generatedSourcesDirectory>${project.build.directory}/generated-sources/annotations</generatedSourcesDirectory>
						<annotationProcessors>
							<annotationProcessor>cz.gattserver.binder.BinderProcessor</annotationProcessor>
						</annotationProcessors>
					</configuration>
				</plugin>

				<!-- To recognize generated files as sources and put them on buildpath -->
				<plugin>
					<groupId>org.codehaus.mojo</groupId>
					<artifactId>build-helper-maven-plugin</artifactId>
					<version>3.0.0</version>
					<executions>
						<execution>
							<id>add-source</id>
							<phase>generate-sources</phase>
							<goals>
								<goal>add-source</goal>
							</goals>
							<configuration>
								<sources>
									<source>${project.build.directory}/generated-sources/annotations</source>
								</sources>
							</configuration>
						</execution>
					</executions>
				</plugin>
			</plugins>
		</build>
	 
	</project>
