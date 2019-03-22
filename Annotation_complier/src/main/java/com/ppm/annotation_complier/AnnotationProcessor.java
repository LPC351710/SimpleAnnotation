package com.ppm.annotation_complier;

import com.google.auto.service.AutoService;
import com.ppm.annotation.BindView;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        System.out.println("getSupportedSourceVersion : ----------------- " + SourceVersion.latestSupported());
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("-------------------------------process-----------------------------");

//        VariableElement 成员遍历
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        Map<String, List<VariableElement>> cacheMap = new HashMap<>();
        for (Element element : elementSet) {
            if (element instanceof VariableElement) {
                VariableElement variableElement = (VariableElement) element;
                String activityName = getActivityName(variableElement);

                List<VariableElement> list = cacheMap.get(activityName);
                if (list == null) {
                    list = new ArrayList<>();
                    cacheMap.put(activityName, list);
                }

                list.add(variableElement);

                System.out.println("variableElement: ========================== " + variableElement);
            }
        }

        Iterator iterator = cacheMap.keySet().iterator();
        while (iterator.hasNext()) {
            String activityName = (String) iterator.next();

            List<VariableElement> variableElementList = cacheMap.get(activityName);

            String activityBinder = activityName + "$ViewBinder";

            Filer filer = processingEnv.getFiler();
            try {
                JavaFileObject javaFileObject = filer.createSourceFile(activityBinder);
                String packageName = getPackageName(variableElementList.get(0));
                Writer writer = javaFileObject.openWriter();
                String activitySimpleName = variableElementList.get(0).getEnclosingElement()
                        .getSimpleName().toString() + "$ViewBinder";
                writeHeader(writer, packageName, activityName, activitySimpleName);

                for (VariableElement variableElement : variableElementList) {
                    BindView bindView = variableElement.getAnnotation(BindView.class);
                    int id = bindView.value();
                    String fieldName = variableElement.getSimpleName().toString();
                    TypeMirror typeMirror = variableElement.asType();
                    writer.write("target." + fieldName + "=(" + typeMirror.toString() + ")target.findViewById(" + id + ");");
                    writer.write("\n");
                }

                writer.write("\n");
                writer.write("}");
                writer.write("\n");
                writer.write("}");
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    private void writeHeader(Writer writer, String packageName, String activityName, String activitySimpleName) {
        try {
            writer.write("package " + packageName + ";");
            writer.write("\n");
            writer.write("import com.ppm.simpleannotation.ViewBinder;");
            writer.write("\n");
            writer.write("public class " + activitySimpleName + " implements ViewBinder<" + activityName + "> {");
            writer.write("\n");
            writer.write("public void bind(" + activityName + " target) {");
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getActivityName(VariableElement variableElement) {
        String packageName = getPackageName(variableElement);
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        return packageName + "." + typeElement.getSimpleName().toString();
    }

    private String getPackageName(VariableElement variableElement) {
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        System.out.println("packageName: " + packageName);
        return packageName;
    }
}
