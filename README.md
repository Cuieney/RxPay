![](https://github.com/Cuieney/RxPay/blob/master/img/logo.png)


## What's RxPay ?

**让支付从此简单下去，一键支付功能，支持支付宝支付，微信支付**

## 使用步骤
### step 1（Gradle）
#### java 项目配置

```
dependencies {
    	compile 'com.cuieney:rxpay-api:2.1.8'
    	annotationProcessor 'com.cuieney:rxpay-compiler:2.1.1'
        //如果你项目配置了kotlin请忽略下面这行的配置（否则会报错 Failed resolution of: Lkotlin/jvm/internal/Intrinsics）
        compile "org.jetbrains.kotlin:kotlin-stdlib-jre7:$kotlin_version"
}

```

#### kotlin 项目配置

```

apply plugin: 'kotlin-kapt'

dependencies {
    compile 'com.cuieney:rxpay-api:2.1.8'
    kapt 'com.cuieney:rxpay-compiler:2.1.1'
    ...
}

```


### step 2
在你的AndroidManifest文件中添加权限
#### AndroidManifest
```

<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

```
### step 3
如果你需要用到微信支付的话，请仔细看一下步骤
1.在你要使用微信支付的地方添加一下注解

```
@WX(packageName = "微信支付注册keystore时候的包名")
public class MainActivity extends AppCompatActivity
```
2.在AndroidManifest添加你微信支付的appid 和PARTNER_ID商户号(固定不变的)，apiKey（商户平台设置的密钥key获取方法，可以问后台要可以到微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置）

```
    //WX_APPID必填项
   <meta-data
            android:name="WX_APPID"
            android:value="xxxxx"/>
   //非必填项，此处填写后，请求json的partnerId字段就可以不填
   <meta-data
            android:name="PARTNER_ID"
            android:value="xxxx"/>
    //非必填项，此处填写后，请求json的sign字段就可以不填（即App端签名）
   <meta-data
            android:name="API_KEY"
            android:value="xxxxx"/>

```
3.在AndroidManifest的微信支付回调页面的Activity

```
     <activity
            android:name="xxx.xxx.xxx.wxapi.WXPayEntryActivity"
            android:exported="true"
            android:screenOrientation="portrait"
            android:windowSoftInputMode="adjustPan"
            />

```
上面的xxx.xxx.xxx就是你微信支付注册keystore时候的包名，报错没关系，编译会生成对应的Activity。

对应的支付宝支付AndroidManifest需要添加的是

```
<activity
    android:name="com.alipay.sdk.app.H5PayActivity"
    android:configChanges="orientation|keyboardHidden|navigation|screenSize"
    android:exported="false"
    android:screenOrientation="behind"
    android:windowSoftInputMode="adjustResize|stateHidden" >
</activity>
 <activity
    android:name="com.alipay.sdk.app.H5AuthActivity"
    android:configChanges="orientation|keyboardHidden|navigation"
    android:exported="false"
    android:screenOrientation="behind"
    android:windowSoftInputMode="adjustResize|stateHidden" >
</activity>

```


### step 4
发起支付功能

1.发起支付宝支付请求

```
 rxPay.requestAlipay("服务器产生的订单信息")
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        payState.setText("阿里支付状态："+aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        payState.setText("阿里支付状态："+throwable.getMessage());
                    }
                });

```

2.发起微信支付请求

```
 rxPay.requestWXpay((“服务器生成订单的后拼接成下图这种json字符串”))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        payState.setText("微信支付状态："+aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        payState.setText("微信支付状态："+throwable.getMessage());
                    }
                });
```
对应的json格式参考


```
1.格式一（Manifest三个参数全设置了）
{
    "prepayId": "必填项",
}
2.格式二（Manifest设置了PARTNER_ID 没有设置API_KEY）
{
    "nonceStr": "必填项",
    "partnerId": "必填项",
    "packageValue": "必填项",
    "prepayId": "必填项",
    "sign": "必填项",
    "timeStamp": "必填项"
}

```


[code sample](https://github.com/Cuieney/RxPay/tree/master/app/src/main/java/com/cuieney/android/rxpay)

#### 混淆

```
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

-dontwarn com.alipay.android.phone.mrpc.core.**

```
#### Tips
* 对于调起微信支付的json的字段请参考以上的json
* 以下的过度版本可以解决依赖包冲突问题
    * com.cuieney:rxpay-api:**2.1.11** 版本 **阿里jar包 微信依赖** **都已剔除**
    * com.cuieney:rxpay-api:**2.1.12** 版本**只剔除 阿里jar包**
    * com.cuieney:rxpay-api:**2.1.13** 版本**只剔除 微信依赖**

* 如果项目中还有Rxjava版本1的话为了防止代码冲突 请在build.gradle里面添加一下代码

```
 packagingOptions {
        exclude 'META-INF/rxjava.properties'
    }

```

#### 问题
发现bug或好的建议欢迎 [issues](https://github.com/Cuieney/RxPay/issues) or
Email <cuieney@163.com>

#### 微信交流群
![](https://github.com/Cuieney/RxPay/blob/master/img/wechat.png)


![](https://github.com/Cuieney/RxPay/blob/master/img/myWechat.png)
过期加我微信拉你进群

### License

> ```
> Copyright 2017 Cuieney
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.


