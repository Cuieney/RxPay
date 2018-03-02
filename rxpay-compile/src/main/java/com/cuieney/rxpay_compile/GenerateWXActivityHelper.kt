package com.cuieney.rxpay_compile

import com.cuieney.rxpay_compile.Consts.ACTIVITY_CN
import com.cuieney.rxpay_compile.Consts.BASEREQ_CN
import com.cuieney.rxpay_compile.Consts.BASERESP_CN
import com.cuieney.rxpay_compile.Consts.BUNDLE_CN
import com.cuieney.rxpay_compile.Consts.INTENT_CN
import com.cuieney.rxpay_compile.Consts.IWXAPI
import com.cuieney.rxpay_compile.Consts.IWXAPI_EVENT_HANDLER
import com.cuieney.rxpay_compile.Consts.LOG_CN
import com.cuieney.rxpay_compile.Consts.PAYMENT_CN
import com.cuieney.rxpay_compile.Consts.RXBUS_CN
import com.cuieney.rxpay_compile.Consts.WARNING_TIPS
import com.cuieney.rxpay_compile.Consts.WXAPI_CN
import com.cuieney.rxpay_compile.Consts.WXPAYWAT_CN
import com.squareup.javapoet.*
import java.io.IOException
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.annotation.processing.Filer


/**
 * To generate the act tool class
 */

class GenerateWXActivityHelper
/**
 *
 * @param packageName When registered appid package name from wechat pay
 */
(private val packageName: String) {

    /**
     * Generate OnCreate method
     * @return
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun generateOnCreate(): MethodSpec.Builder {
        val paramSpec = ParameterSpec.builder(BUNDLE_CN, "savedInstanceState").build()

        val onCreateMethod = MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override::class.java)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec)
        onCreateMethod.addStatement(
                "super.onCreate(savedInstanceState);\n" +
                        "String appId = \$T.INSTANCE.getMetaData(this,\"WX_APPID\");\n" +
                        "api = \$T.createWXAPI(this, appId);\n" +
                        "api.handleIntent(getIntent(), this);", WXPAYWAT_CN, WXAPI_CN)

        return onCreateMethod
    }

    /**
     * Generate OnNewIntent method
     * @return
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun generateOnNewIntent(): MethodSpec.Builder {
        val paramSpec = ParameterSpec.builder(INTENT_CN, "intent").build()

        val onNewIntentMethod = MethodSpec.methodBuilder("onNewIntent")
                .addAnnotation(Override::class.java)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec)

        onNewIntentMethod.addStatement(
                ("super.onNewIntent(intent);\n"
                        + "setIntent(intent);\n"
                        + "api.handleIntent(intent, this);"))

        return onNewIntentMethod
    }

    /**
     * implement IWXAPIEventHandler's method
     * @return
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun generateOnReq(): MethodSpec.Builder {
        val paramSpec = ParameterSpec.builder(BASEREQ_CN, "baseReq").build()

        return MethodSpec.methodBuilder("onReq")
                .addAnnotation(Override::class.java)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec)

    }

    /**
     * implement IWXAPIEventHandler's method
     * @return
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun generateOnResp(): MethodSpec.Builder {
        val paramSpec = ParameterSpec.builder(BASERESP_CN, "baseResp").build()
        val onRespMethod = MethodSpec.methodBuilder("onResp")
                .addAnnotation(Override::class.java)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec)

        onRespMethod.addStatement(
                ("int errCode = baseResp.errCode;\n" +
                        "\$T.e(\"Rxpay\", \"errCode:\" + errCode);\n" +
                        "if (errCode == 0) {\n" +
                        "   \$T.Companion.getDefault().post(new \$T(true));\n" +
                        "}else{\n" +
                        "   \$T.Companion.getDefault().post(new \$T(false));\n" +
                        "}\n" +
                        "finish();\n"),LOG_CN, RXBUS_CN, PAYMENT_CN, RXBUS_CN, PAYMENT_CN)

        return onRespMethod

    }

    /**
     * Generate field api
     * @return
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun generateField(): FieldSpec {
        return FieldSpec.builder(IWXAPI, "api")
                .addModifiers(PRIVATE)
                .build()
    }


    /**
     * Generate WXPayEntryActivity
     * @param mFiler
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    fun generateAct(mFiler: Filer) {
        val typeSpec = TypeSpec.classBuilder("WXPayEntryActivity")
                .addModifiers(PUBLIC)
                .addJavadoc(WARNING_TIPS)
                .addSuperinterface(IWXAPI_EVENT_HANDLER)
                .superclass(ACTIVITY_CN)
                .addMethod(generateOnCreate().build())
                .addMethod(generateOnNewIntent().build())
                .addMethod(generateOnReq().build())
                .addMethod(generateOnResp().build())
                .addField(generateField())
                .build()
        val javaFile = JavaFile.builder(packageName, typeSpec)
                .build()
        javaFile.writeTo(mFiler)

    }
}