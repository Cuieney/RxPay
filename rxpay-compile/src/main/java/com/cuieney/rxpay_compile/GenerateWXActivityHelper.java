package com.cuieney.rxpay_compile;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;

import static com.cuieney.rxpay_compile.Consts.*;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;


/**
 * To generate the act tool class
 */

public class GenerateWXActivityHelper {
    private String packageName;

    /**
     *
     * @param packageName When registered appid package name from wechat pay
     */
    public GenerateWXActivityHelper(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Generate OnCreate method
     * @return
     * @throws ClassNotFoundException
     */
    private MethodSpec.Builder  generateOnCreate() throws ClassNotFoundException {
        ParameterSpec paramSpec = ParameterSpec.builder(BUNDLE_CN, "savedInstanceState").build();

        MethodSpec.Builder onCreateMethod = MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec);
        onCreateMethod.addStatement(
                "super.onCreate(savedInstanceState);\n" +
                        "String appId = $T.getAppId(this);\n" +
                        "api = $T.createWXAPI(this, appId);\n" +
                        "api.handleIntent(getIntent(), this);",WXPAYWAT_CN,WXAPI_CN);

        return onCreateMethod;
    }

    /**
     * Generate OnNewIntent method
     * @return
     * @throws ClassNotFoundException
     */
    private MethodSpec.Builder generateOnNewIntent() throws ClassNotFoundException {
        ParameterSpec paramSpec = ParameterSpec.builder(INTENT_CN, "intent").build();

        MethodSpec.Builder onNewIntentMethod = MethodSpec.methodBuilder("onNewIntent")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec);

        onNewIntentMethod.addStatement(
                "super.onNewIntent(intent);\n"
                        + "setIntent(intent);\n"
                        + "api.handleIntent(intent, this);");

        return onNewIntentMethod;
    }

    /**
     * implement IWXAPIEventHandler's method
     * @return
     * @throws ClassNotFoundException
     */
    private MethodSpec.Builder generateOnReq() throws ClassNotFoundException {
        ParameterSpec paramSpec = ParameterSpec.builder(BASEREQ_CN, "baseReq").build();

        MethodSpec.Builder onReqMethod = MethodSpec.methodBuilder("onReq")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec);
        return onReqMethod;

    }

    /**
     * implement IWXAPIEventHandler's method
     * @return
     * @throws ClassNotFoundException
     */
    private MethodSpec.Builder generateOnResp() throws ClassNotFoundException {
        ParameterSpec paramSpec = ParameterSpec.builder(BASERESP_CN, "baseResp").build();
        MethodSpec.Builder onRespMethod = MethodSpec.methodBuilder("onResp")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec);

        onRespMethod.addStatement(
                "int errCode = baseResp.errCode;" +
                        "if (errCode == 0) {\n" +
                        "   $T.getDefault().post(new $T(true));\n" +
                        "}else{\n" +
                        "   $T.getDefault().post(new $T(false));\n" +
                        "}\n" +
                        "finish();\n",RXBUS_CN,PAYMENT_CN,RXBUS_CN,PAYMENT_CN);

        return onRespMethod;

    }

    /**
     * Generate field api
     * @return
     * @throws ClassNotFoundException
     */
    private FieldSpec generateField() throws ClassNotFoundException {
        FieldSpec fieldSpec = FieldSpec.builder(IWXAPI, "api")
                .addModifiers(PRIVATE)
                .build();
        return fieldSpec;
    }


    /**
     * Generate WXPayEntryActivity
     * @param mFiler
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void generateAct(Filer mFiler) throws IOException, ClassNotFoundException {
        TypeSpec typeSpec = TypeSpec.classBuilder("WXPayEntryActivity")
                .addModifiers(PUBLIC)
                .addJavadoc(WARNING_TIPS)
                .addSuperinterface(IWXAPI_EVENT_HANDLER)
                .superclass(ACTIVITY_CN)
                .addMethod(generateOnCreate().build())
                .addMethod(generateOnNewIntent().build())
                .addMethod(generateOnReq().build())
                .addMethod(generateOnResp().build())
                .addField(generateField())
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();
        javaFile.writeTo(mFiler);

    }
}