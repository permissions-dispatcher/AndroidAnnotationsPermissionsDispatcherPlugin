package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherplugin;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EComponentHolder;

import java.lang.reflect.Field;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class NeedsPermissionHandler extends BaseAnnotationHandler<EComponentHolder> {

    public static final String PERMISSION_WRITE_SETTINGS = "\"android.permission.WRITE_SETTINGS\"";
    public static final String PERMISSION_SYSTEM_ALERT_WINDOW = "\"android.permission.SYSTEM_ALERT_WINDOW\"";

    public NeedsPermissionHandler(AndroidAnnotationsEnvironment environment) {
        super("permissions.dispatcher.NeedsPermission", environment);
    }

    @Override
    protected void validate(Element element, ElementValidation validation) {
        if (validatorHelper.elementHasAnnotation(EActivity.class, element.getEnclosingElement())
                || validatorHelper.elementHasAnnotation(EFragment.class, element.getEnclosingElement())) {
            validatorHelper.isNotPrivate(element, validation);
            validatorHelper.isNotFinal(element, validation);
            validatorHelper.returnTypeIsVoid((ExecutableElement) element, validation);
        }
    }

    @Override
    public void process(Element element, EComponentHolder holder) throws Exception {
        TypeElement annotatedElement = holder.getAnnotatedElement();
        if (annotatedElementHasRuntimePermissions(annotatedElement)) {
            String delegateClassName = annotatedElement.getQualifiedName().toString() + "PermissionsDispatcher";
            AbstractJClass delegateClass = getJClass(delegateClassName);
            PermissionDispatcherHolder permissionDispatcherHolder = holder.getPluginHolder(new PermissionDispatcherHolder(holder));

            setDispatcherCallbacks(element, delegateClass, permissionDispatcherHolder);
            JFieldVar dispatcherCalledField = permissionDispatcherHolder.getPermissionDispatcherCalledField();
            setPermissionMethods(element, holder, delegateClass, dispatcherCalledField);
        }
    }

    private boolean annotatedElementHasRuntimePermissions(TypeElement annotatedElement) {
        return annotatedElement.getAnnotationMirrors().stream()
                .map(annotationMirror -> annotationMirror.getAnnotationType().asElement().getSimpleName().toString())
                .anyMatch(annotation -> annotation.contains("RuntimePermissions"));
    }

    private void setPermissionMethods(Element element, EComponentHolder holder, AbstractJClass delegateClass, JFieldVar dispatcherCalledField) {
        ExecutableElement executableElement = (ExecutableElement) element;

        JMethod overrideMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
        JBlock previousMethodBody = codeModelHelper.removeBody(overrideMethod);

        JBlock overrideMethodBody = overrideMethod.body();
        JConditional conditional = overrideMethodBody._if(dispatcherCalledField.not());

        JBlock thenBlock = conditional._then();
        thenBlock.assign(dispatcherCalledField, JExpr.TRUE);
        String delegateMethodName = element.getSimpleName().toString() + "WithPermissionCheck";

        JInvocation delegateCall = delegateClass.staticInvoke(delegateMethodName)
                .arg(JExpr._this());

        overrideMethod.params().forEach(delegateCall::arg);

        if (overrideMethod.hasVarArgs()) {
            JVar jVar = overrideMethod.varParam();
            if (jVar != null) {
                delegateCall.arg(jVar);
            }
        }

        if (!removeRuntimePermissionsAnnotation(holder.getGeneratedClass())) {
            codeModelHelper.copyAnnotation(overrideMethod, findAnnotation(element));
        }

        thenBlock.add(delegateCall);

        JBlock elseBlock = conditional._else();
        elseBlock.assign(dispatcherCalledField, JExpr.FALSE);
        elseBlock.add(previousMethodBody);
    }

    private void setDispatcherCallbacks(Element element, AbstractJClass delegateClass, PermissionDispatcherHolder permissionDispatcherHolder) {
        if (hasSpecialPermissions(element)) {
            permissionDispatcherHolder.setOnActivityResultDelegateCall(delegateClass);
        }
        if (hasNormalPermissions(element)) {
            permissionDispatcherHolder.setOnRequestPermissionsResultDelegateCall(delegateClass);
        }
    }

    private AnnotationMirror findAnnotation(Element element) {
        return element.getAnnotationMirrors().stream()
                .filter(this::isNeedsPermission)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Handled annotation should be on the method!"));
    }

    private boolean hasSpecialPermissions(Element element) {
        return element.getAnnotationMirrors().stream()
                .filter(this::isNeedsPermission)
                .anyMatch(this::hasSpecialPermissions);
    }

    private boolean hasNormalPermissions(Element element) {
        return element.getAnnotationMirrors().stream()
                .filter(this::isNeedsPermission)
                .anyMatch(annotationMirror -> !hasSpecialPermissions(annotationMirror));
    }

    private boolean hasSpecialPermissions(AnnotationMirror annotationMirror) {
        String name = annotationMirror.toString();
        return name.contains(PERMISSION_SYSTEM_ALERT_WINDOW) || name.contains(PERMISSION_WRITE_SETTINGS);
    }

    private boolean isNeedsPermission(AnnotationMirror annotationMirror) {
        return annotationMirror.getAnnotationType().asElement().getSimpleName().toString().equals("NeedsPermission");
    }

    @SuppressWarnings("unchecked")
    private boolean removeRuntimePermissionsAnnotation(JDefinedClass definedClass) {
        try {
            Field annotationsField = definedClass.getClass().getDeclaredField("m_aAnnotations");
            annotationsField.setAccessible(true);
            List<JAnnotationUse> annotations = (List<JAnnotationUse>) annotationsField.get(definedClass);
            if (annotations == null) {
                return true;
            }
            annotations.removeIf(jAnnotationUse -> jAnnotationUse.getAnnotationClass().name().equals("RuntimePermissions"));
            return true;
        } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}