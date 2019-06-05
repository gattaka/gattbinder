package cz.gattserver.binder.plugin;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * @goal process
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @author gattaka
 */
@Mojo(defaultPhase = LifecyclePhase.GENERATE_SOURCES, name = "generate",
		requiresDependencyResolution = ResolutionScope.COMPILE)
public class BinderMojo extends AbstractMojo {

	private static final String JAVA_FILE_FILTER = "/*.java";

	private BuildContext buildContext;

	@Parameter(property = "outputDirectory", defaultValue = "target/generated-sources/bindings")
	private String outputDirectory;

	@Parameter(defaultValue = "${plugin}", readonly = true)
	private PluginDescriptor pluginDescriptor;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	public void execute() throws MojoExecutionException {
		getLog().info("Gattserver Binder generating...");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new MojoExecutionException("You need to run build with JDK or have tools.jar on the classpath."
					+ "If this occures during eclipse build make sure you run eclipse under JDK as well");
		}

	}

}