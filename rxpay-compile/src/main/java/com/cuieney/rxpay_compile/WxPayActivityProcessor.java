package com.cuieney.rxpay_compile;

import com.cuieney.rxpay_annotation.WX;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import static com.cuieney.rxpay_compile.Consts.ANNOTATION_TYPE_WX;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ANNOTATION_TYPE_WX})
public class WxPayActivityProcessor extends AbstractProcessor {
    private Filer mFiler;
    Messager msg;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        msg = processingEnvironment.getMessager();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(com.cuieney.rxpay_annotation.WX.class);
        for (Element element : routeElements) {
            WX wx = element.getAnnotation(WX.class);
            String packageName = wx.packageName();
            if (packageName.isEmpty()) {
                return false;
            }
            try {
                new GenerateWXActivityHelper(packageName+".wxapi").generateAct(mFiler);
            } catch (IOException e) {
                msg.printMessage(Diagnostic.Kind.OTHER, "Rxpay: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                msg.printMessage(Diagnostic.Kind.OTHER, "Rxpay: " + e.getMessage());
            }
        }
        return false;
    }


}
