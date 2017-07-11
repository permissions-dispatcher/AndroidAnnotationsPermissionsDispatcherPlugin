package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherplugin;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.plugin.PluginClassHolder;

public class PermissionDispatcherHolder extends PluginClassHolder<EComponentHolder> {

	private JFieldVar permissionDispatcherCalledField;
	private JMethod onRequestPermissionsResultMethod;
	private JBlock onRequestPermissionsResultMethodDelegateBlock;
	private JVar requestCodeParam;
	private JVar grantResultsParam;

	public PermissionDispatcherHolder(EComponentHolder holder) {
		super(holder);
	}

	public JFieldVar getPermissionDispatcherCalledField() {
		if (permissionDispatcherCalledField == null) {
			setPermissionDispatcherCalledField();
		}

		return permissionDispatcherCalledField;
	}

	private void setPermissionDispatcherCalledField() {
		permissionDispatcherCalledField = holder().getGeneratedClass().field(JMod.PRIVATE, getCodeModel().BOOLEAN, "permissionDispatcherCalled_");
	}

	public JMethod getOnRequestPermissionsResult() {
		if (onRequestPermissionsResultMethod == null) {
			setOnRequestPermissionsResultMethod();
		}

		return onRequestPermissionsResultMethod;
	}

	private void setOnRequestPermissionsResultMethod() {
		onRequestPermissionsResultMethod = holder().getGeneratedClass().method(JMod.PUBLIC, getCodeModel().VOID, "onRequestPermissionsResult");
		onRequestPermissionsResultMethod.annotate(Override.class);

		requestCodeParam = onRequestPermissionsResultMethod.param(getCodeModel().INT, "requestCode");
		JVar permissionsParam = onRequestPermissionsResultMethod.param(getJClass("java.lang.String").array(), "permissions");
		grantResultsParam = onRequestPermissionsResultMethod.param(getCodeModel().INT.array(), "grantResults");

		JBlock onRequestPermissionsResultMethodBody = onRequestPermissionsResultMethod.body();

		onRequestPermissionsResultMethodBody.invoke(JExpr._super(), "onRequestPermissionsResult")
				.arg(requestCodeParam)
				.arg(permissionsParam)
				.arg(grantResultsParam);

		onRequestPermissionsResultMethodDelegateBlock = onRequestPermissionsResultMethodBody.blockVirtual();

		onRequestPermissionsResultMethodBody.assign(getPermissionDispatcherCalledField(), JExpr.FALSE);
	}

	public void setDelegateCall(AbstractJClass delegateClass) {
		if (onRequestPermissionsResultMethod != null) {
			return;
		}

		setOnRequestPermissionsResultMethod();

		onRequestPermissionsResultMethodDelegateBlock.add(delegateClass.staticInvoke("onRequestPermissionsResult")
				.arg(JExpr._this())
				.arg(requestCodeParam)
				.arg(grantResultsParam));
	}

}
