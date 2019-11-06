package cz.gattserver.binder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("cz.gattserver.binder.GenerateBinding")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BinderProcessor extends AbstractProcessor {

	private int level = 0;

	private String align(String value) {
		String spaces = "\t";
		for (int i = 0; i < level; i++)
			spaces += "\t";
		return spaces + value;
	}

	private void log(String value) {
		System.out.println(align(value));
	}

	private void err(String value) {
		System.err.println(align(value));
	}

	private String lowerFirstLetter(String string) {
		String prefix = string.substring(0, 1);
		String rest = string.substring(1);
		return prefix.toLowerCase() + rest;
	}

	private void scanMethodsForGetters(Set<String> getters, List<ExecutableElement> executableElements) {
		level++;
		for (ExecutableElement ee : executableElements) {
			String name = ee.getSimpleName().toString();
			if (name.startsWith("get")) {
				String result = lowerFirstLetter(name.substring(3));
				getters.add(result);
				log(name + " -> " + result);
			} else if (name.startsWith("is")) {
				String result = lowerFirstLetter(name.substring(2));
				getters.add(result);
				log(name + " -> " + result);
			}
		}
		level--;
	}

	private String scanTypeForGetters(Types types, Set<String> getters, Element element) {
		level++;

		TypeMirror typeMirror = element.asType();
		String className = typeMirror.toString();

		if (!className.equals(Object.class.getCanonicalName())) {
			log("Class: " + className);
			scanMethodsForGetters(getters, ElementFilter.methodsIn(element.getEnclosedElements()));

			for (TypeMirror supertype : types.directSupertypes(typeMirror)) {
				if (supertype instanceof DeclaredType) {
					DeclaredType declared = (DeclaredType) supertype;
					Element superTypeElement = declared.asElement();

					scanTypeForGetters(types, getters, superTypeElement);
				}
			}
		}

		level--;
		return typeMirror.toString();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		log("BinderProcessor processing...");

		Types types = processingEnv.getTypeUtils();

		for (TypeElement annotation : annotations) {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
			for (Element element : elements) {
				Set<String> getters = new HashSet<>();
				String typeName = scanTypeForGetters(types, getters, element);

				try {
					writeFile(typeName, getters);
				} catch (IOException e) {
					err("Class: " + typeName + " processing failed (" + e.getMessage() + ")");
				}
			}
		}
		return true;
	}

	private void writeFile(String className, Set<String> getters) throws IOException {
		String packageName = null;
		int lastDot = className.lastIndexOf('.');
		if (lastDot > 0)
			packageName = className.substring(0, lastDot);

		// String simpleClassName = className.substring(lastDot + 1);
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

			for (String getter : getters) {
				out.print("	public final String ");
				out.print(getter);
				out.print(" = \"");
				out.print(getter);
				out.println("\";");
			}

			out.println();
			out.println("}");
		}
	}
}