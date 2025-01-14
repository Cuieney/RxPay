package com.cuieney.rxpay_compile

import com.cuieney.rxpay_compile.Constant.ACTIVITY_CN
import com.cuieney.rxpay_compile.Constant.BASEREQ_CN
import com.cuieney.rxpay_compile.Constant.BASERESP_CN
import com.cuieney.rxpay_compile.Constant.BUNDLE_CN
import com.cuieney.rxpay_compile.Constant.INTENT_CN
import com.cuieney.rxpay_compile.Constant.IWXAPI
import com.cuieney.rxpay_compile.Constant.IWXAPI_EVENT_HANDLER
import com.cuieney.rxpay_compile.Constant.LOG_CN
import com.cuieney.rxpay_compile.Constant.PAYMENT_CN
import com.cuieney.rxpay_compile.Constant.RXBUS_CN
import com.cuieney.rxpay_compile.Constant.WARNING_TIPS
import com.cuieney.rxpay_compile.Constant.WXAPI_CN
import com.cuieney.rxpay_compile.Constant.WXPAYWAT_CN
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC


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

        onCreateMethod.addStatement("super.onCreate(savedInstanceState)")
        onCreateMethod.addStatement(
            "String appId = \$T.INSTANCE.getMetaData(this,\"WX_APPID\")",
            WXPAYWAT_CN
        )
        onCreateMethod.addStatement("mWXAPI = \$T.createWXAPI(this, appId)", WXAPI_CN)
        onCreateMethod.addStatement("mWXAPI.handleIntent(getIntent(), this)")

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

        onNewIntentMethod.addStatement("super.onNewIntent(intent)")
        onNewIntentMethod.addStatement("setIntent(intent)")
        onNewIntentMethod.addStatement("mWXAPI.handleIntent(intent, this)")

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

        onRespMethod.addStatement("int errCode = baseResp.errCode")
        onRespMethod.addStatement("\$T.e(\"RxPay\", \"WXPayErrCode:\" + errCode)", LOG_CN)
        onRespMethod.addStatement(
            "if (errCode == 0) \n" +
                    "\$T.Companion.getDefault().post(new \$T(true))", RXBUS_CN, PAYMENT_CN
        )
        onRespMethod.addStatement(
            "else \n" +
                    "\$T.Companion.getDefault().post(new \$T(false))", RXBUS_CN, PAYMENT_CN
        )
        onRespMethod.addStatement("finish()")
        return onRespMethod
    }

    /**
     * Generate field mWXAPI
     * @return
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun generateField(): FieldSpec {
        return FieldSpec.builder(IWXAPI, "mWXAPI")
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