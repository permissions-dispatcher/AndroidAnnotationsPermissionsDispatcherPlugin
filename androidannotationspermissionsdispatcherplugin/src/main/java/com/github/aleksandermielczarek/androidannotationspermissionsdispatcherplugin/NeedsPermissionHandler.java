package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherplugin;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;
import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EComponentHolder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class NeedsPermissionHandler extends BaseAnnotationHandler<EComponentHolder> {

	public NeedsPermissionHandler(AndroidAnnotationsEnvironment environment) {
		super("permissions.dispatcher.NeedsPermission", environment);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validation);
		validatorHelper.isNotPrivate(element, validation);
		validatorHelper.isNotFinal(element, validation);
		validatorHelper.returnTypeIsVoid((ExecutableElement) element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		TypeElement annotatedElement = holder.getAnnotatedElement();
		String delegateClassName = annotatedElement.getQualifiedName().toString() + "PermissionsDispatcher";
		AbstractJClass delegateClass = getJClass(delegateClassName);

		PermissionDispatcherHolder permissionDispatcherHolder = holder.getPluginHolder(new PermissionDispatcherHolder(holder));
		permissionDispatcherHolder.setDelegateCall(delegateClass);
		JFieldVar dispatcherCalledField = permissionDispatcherHolder.getPermissionDispatcherCalledField();

		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod overrideMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JBlock previousMethodBody = codeModelHelper.removeBody(overrideMethod);

		JBlock overrideMethodBody = overrideMethod.body();
		JConditional conditional = overrideMethodBody._if(dispatcherCalledField.not());

		JBlock thenBlock = conditional._then();
		thenBlock.assign(dispatcherCalledField, JExpr.TRUE);
		String delegateMethodName = element.getSimpleName().toString() + "WithCheck";

		JInvocation delegateCall = delegateClass.staticInvoke(delegateMethodName)
				.arg(JExpr._this());

		for (JVar param : overrideMethod.params()) {
			delegateCall.arg(param);
		}

		if (overrideMethod.hasVarArgs()) {
			delegateCall.arg(overrideMethod.varParam());
		}

		thenBlock.add(delegateCall);

		JBlock elseBlock = conditional._else();
		elseBlock.assign(dispatcherCalledField, JExpr.FALSE);
		elseBlock.add(previousMethodBody);
	}
}
