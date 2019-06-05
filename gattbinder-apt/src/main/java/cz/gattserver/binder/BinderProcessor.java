package cz.gattserver.binder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("cz.gattserver.binder.GenerateBinding")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BinderProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println("BinderProcessor processing...");

		for (TypeElement annotation : annotations) {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
			for (Element element : elements) {
				TypeMirror classType = element.asType();
				String className = classType.toString();

				List<VariableElement> fields = ElementFilter.fieldsIn(element.getEnclosedElements());
				try {
					writeFile(classType.toString(), fields);
				} catch (IOException e) {
					System.err.println("Class: " + className + " processing failed (" + e.getMessage() + ")");
				}
			}
		}
		return true;
	}
	
//	private List<VariableElement> tryGatherParentFields(Element element) {
//		List<VariableElement> fields = ElementFilter.fieldsIn(element.getEnclosingElement());
//	}

	private void writeFile(String className, List<VariableElement> fields) throws IOException {
		System.out.println("\tClass: " + className);

		String packageName = null;
		int lastDot = className.lastIndexOf('.');
		if (lastDot > 0) {
			packageName = className.substring(0, lastDot);
		}

		String simpleClassName = className.substring(lastDot + 1);
		String bindClassName = className + "Bind";
		String bindSimpleClassName = bindClassName.substring(lastDot + 1);

		JavaFileObject bindFile = processingEnv.getFiler().createSourceFile(bindClassName);

		try (PrintWriter out = new PrintWriter(bindFile.openWriter())) {

			if (packageName != null) {
				out.print("package ");
				out.print(packageName);
				out.println(";");
				out.println();
			}

			out.print("public class ");
			out.print(bindSimpleClassName);
			out.println(" {");
			out.println();

			for (VariableElement field : fields) {
				System.out.println("\t\tField: " + field.getSimpleName());
				out.print("	public final String ");
				out.print(field.getSimpleName());
				out.print(" = \"");
				out.print(field.getSimpleName());
				out.println("\";");
			}

			out.println();
			out.println("}");
		}
	}
}