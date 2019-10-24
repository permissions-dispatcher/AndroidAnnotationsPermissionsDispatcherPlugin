[![](https://jitpack.io/v/AleksanderMielczarek/AndroidAnnotationsPermissionsDispatcherPlugin.svg)](https://jitpack.io/#AleksanderMielczarek/AndroidAnnotationsPermissionsDispatcherPlugin)

# AndroidAnnotationsPermissionsDispatcherPlugin

Plugin for [AndroidAnnotations](http://androidannotations.org/) allowing to use it together with [PermissionsDispatcher](http://hotchemi.github.io/PermissionsDispatcher/).
All you have to do is to include it in your dependencies.

## Usage

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

Add the dependency

```groovy
dependencies {
    compile "com.github.hotchemi:permissionsdispatcher:${permissionsdispatcherVersion}"
    annotationProcessor "com.github.hotchemi:permissionsdispatcher-processor:${permissionsdispatcherVersion}"
    compile "org.androidannotations:androidannotations-api:${androidannotationsVersion}"
    annotationProcessor "org.androidannotations:androidannotations:${androidannotationsVersion}"
    annotationProcessor "com.github.AleksanderMielczarek:AndroidAnnotationsPermissionsDispatcherPlugin:${latestVersion}"
}
```

## Example

```java
@EActivity(R.layout.activity_main)
@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @Click(R.id.permissionButton)
    protected void askForPermission() {
        showCamera();
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    protected void showCamera() {
        Toast.makeText(this, "Permission for camera granted", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    protected void onPermissionDeniedCamera() {
        Toast.makeText(this, "@OnPermissionDenied for camera", Toast.LENGTH_SHORT).show();
    }
    
    @OnShowRationale(Manifest.permission.CAMERA)
    protected void showRationaleForCamera(PermissionRequest request) {
        Toast.makeText(this, "OnShowRationale for camera", Toast.LENGTH_SHORT).show();
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    protected void showNeverAskForCamera() {
        Toast.makeText(this, "OnNeverAskAgain for camera", Toast.LENGTH_SHORT).show();
    }

}
```

# Migrating to 2.x.x

Finally, you should not call the PermissionsDispatcher delegate class static methods anymore, this
plugin will generate those calls for you. Just call the method which was annotated with `@NeedsPermission`. Overriding `onRequestPermissionsResult` and `onActivtyResult` (special permissions) should also be removed completely.
See the example project for code. 

## Thanks

* **WonderCsabo** for integrating plugin completely with PermissionsDispatcher

## Changelog

### 2.1.1 (2018-01-24)

- do not generate dispatcher calls if there is no `@RuntimePermissions` (#18)

### 2.1.0 (2017-10-29)

- support PermissionsDispatcher 3.0.0
- does not contain Kotlin support

### 2.0.3 (2017-08-09)

- fix generating duplicated dispatchers (#10)

### 2.0.2 (2017-08-08)

- fix special permissions handling (#9)

### 2.0.1 (2017-08-03)

- fix not compiling code when AA is running first (#7)

### 2.0.0 (2017-07-12)

- fully integration with PermissionsDispatcher

### 1.0.0 (2017-07-02)

- rename module name
- update dependencies

## License

    Copyright 2016 Aleksander Mielczarek

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
