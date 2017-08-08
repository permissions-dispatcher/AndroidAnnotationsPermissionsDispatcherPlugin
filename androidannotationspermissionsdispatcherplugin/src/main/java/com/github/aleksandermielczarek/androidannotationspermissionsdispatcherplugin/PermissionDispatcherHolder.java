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
    private JVar onRequestPermissionsResultGrantResultsParam;
    private JVar onRequestPermissionsResultRequestCodeParam;

    private JMethod onActivityResultMethod;
    private JBlock onActivityResultMethodDelegateBlock;
    private JVar onActivityResultRequestCodeParam;

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

    private void setOnRequestPermissionsResultMethod() {
        onRequestPermissionsResultMethod = holder().getGeneratedClass().method(JMod.PUBLIC, getCodeModel().VOID, "onRequestPermissionsResult");
        onRequestPermissionsResultMethod.annotate(Override.class);

        onRequestPermissionsResultRequestCodeParam = onRequestPermissionsResultMethod.param(getCodeModel().INT, "requestCode");
        JVar permissionsParam = onRequestPermissionsResultMethod.param(getJClass("java.lang.String").array(), "permissions");
        onRequestPermissionsResultGrantResultsParam = onRequestPermissionsResultMethod.param(getCodeModel().INT.array(), "grantResults");

        JBlock onRequestPermissionsResultMethodBody = onRequestPermissionsResultMethod.body();

        onRequestPermissionsResultMethodBody.invoke(JExpr._super(), "onRequestPermissionsResult")
                .arg(onRequestPermissionsResultRequestCodeParam)
                .arg(permissionsParam)
                .arg(onRequestPermissionsResultGrantResultsParam);

        onRequestPermissionsResultMethodDelegateBlock = onRequestPermissionsResultMethodBody.blockVirtual();

        onRequestPermissionsResultMethodBody.assign(getPermissionDispatcherCalledField(), JExpr.FALSE);
    }

    private void setOnActivityResultMethod() {
        onActivityResultMethod = holder().getGeneratedClass().methods().stream()
                .filter(jMethod -> jMethod.name().contains("onActivityResult"))
                .findFirst()
                .orElseGet(() -> {
                    JMethod onActivityResultMethod = holder().getGeneratedClass().method(JMod.PUBLIC, getCodeModel().VOID, "onActivityResult");
                    onActivityResultMethod.annotate(Override.class);

                    onActivityResultRequestCodeParam = onActivityResultMethod.param(getCodeModel().INT, "requestCode");
                    JVar resultCodeParam = onActivityResultMethod.param(getCodeModel().INT, "resultCode");
                    JVar dataParam = onActivityResultMethod.param(getCodeModel().parseType("android.content.Intent"), "data");

                    JBlock onActivityResultMethodBody = onActivityResultMethod.body();

                    onActivityResultMethodBody.invoke(JExpr._super(), "onActivityResult")
                            .arg(onActivityResultRequestCodeParam)
                            .arg(resultCodeParam)
                            .arg(dataParam);
                    return onActivityResultMethod;
                });

        JBlock onActivityResultMethodBody = onActivityResultMethod.body();
        onActivityResultMethodDelegateBlock = onActivityResultMethodBody.blockVirtual();

        if (onActivityResultRequestCodeParam == null) {
            onActivityResultRequestCodeParam = onActivityResultMethod.paramAtIndex(0);
        }

        onActivityResultMethodBody.assign(getPermissionDispatcherCalledField(), JExpr.FALSE);
    }

    public void setOnRequestPermissionsResultDelegateCall(AbstractJClass delegateClass) {
        if (onRequestPermissionsResultMethod != null) {
            return;
        }

        setOnRequestPermissionsResultMethod();

        onRequestPermissionsResultMethodDelegateBlock.add(delegateClass.staticInvoke("onRequestPermissionsResult")
                .arg(JExpr._this())
                .arg(onRequestPermissionsResultRequestCodeParam)
                .arg(onRequestPermissionsResultGrantResultsParam));
    }

    public void setOnActivityResultDelegateCall(AbstractJClass delegateClass) {
        if (onActivityResultMethod != null) {
            return;
        }

        setOnActivityResultMethod();

        onActivityResultMethodDelegateBlock.add(delegateClass.staticInvoke("onActivityResult")
                .arg(JExpr._this())
                .arg(onActivityResultRequestCodeParam));
    }
}