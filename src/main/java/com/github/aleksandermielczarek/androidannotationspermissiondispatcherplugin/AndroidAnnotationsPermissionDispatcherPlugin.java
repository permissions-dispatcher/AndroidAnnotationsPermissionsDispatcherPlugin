package com.github.aleksandermielczarek.androidannotationspermissiondispatcherplugin;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksander Mielczarek on 19.06.2016.
 */
public class AndroidAnnotationsPermissionDispatcherPlugin extends AndroidAnnotationsPlugin {

    @Override
    public String getName() {
        return "AndroidAnnotationsPermissionDispatcherPlugin";
    }

    @Override
    public List<AnnotationHandler<?>> getHandlers(AndroidAnnotationsEnvironment androidAnnotationEnv) {
        List<AnnotationHandler<?>> handlers = new ArrayList<>();
        handlers.add(new RuntimePermissionsHandler(androidAnnotationEnv));
        return handlers;
    }
}
