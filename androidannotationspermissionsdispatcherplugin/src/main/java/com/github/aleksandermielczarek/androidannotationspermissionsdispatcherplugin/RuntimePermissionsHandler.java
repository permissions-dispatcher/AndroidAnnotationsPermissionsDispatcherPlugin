package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherplugin;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EComponentHolder;

import javax.lang.model.element.Element;

/**
 * Created by Aleksander Mielczarek on 19.06.2016.
 */
public class RuntimePermissionsHandler extends BaseAnnotationHandler<EComponentHolder> {

    public RuntimePermissionsHandler(AndroidAnnotationsEnvironment environment) {
        super("permissions.dispatcher.RuntimePermissions", environment);
    }

    @Override
    protected void validate(Element element, ElementValidation validation) {

    }

    @Override
    public void process(Element element, EComponentHolder holder) throws Exception {

    }
}