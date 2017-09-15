
![](https://github.com/Cuieney/RxPay/blob/master/img/logo.png)

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[![Download](https://api.bintray.com/packages/tangsiyuan/maven/pay/images/download.svg) ](https://bintray.com/cui131425/mave/RxPay)

## What's RxPay ?

**让支付从此简单下去，一键支付功能，支持支付宝支付，微信支付**

## 使用步骤
### step 1
#### Gradle

```
dependencies {
    	compile 'com.cuieney.library:rxpay-api:1.0.3'
    	annotationProcessor 'com.cuieney.library:rxpay-compile:1.0.3'
}

```
### step 2
在你的AndroidManifest文件中添加权限
#### AndroidManifest
```
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>

```
### step 3
如果你需要用到微信支付的话，请仔细看一下步骤
1.在你要使用微信支付的地方添加一下注解

```
@WX(packageName = "微信支付注册keystore时候的包名")
public class MainActivity extends AppCompatActivity
```
2.在AndroidManifest添加你微信支付的appid

```
   <meta-data
            android:name="WX_APPID"
            android:value="wxb51b89cba83263"/>
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

### step 4
发起支付功能

1.发起支付宝支付请求

```
 rxPay.requestAlipay("服务器产生的订单号")
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
 rxPay.requestWXpay(new JSONObject(“服务器生成订单的后信息json”))
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

![json.png](http://upload-images.jianshu.io/upload_images/3415839-16341c6eb0f938f8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

[code sample](https://github.com/Cuieney/rxpay/blob/master/app/src/main/java/com/cuieney/rxpay_master/MainActivity.java)

#### 问题
发现bug或好的建议欢迎 [issues](https://github.com/Cuieney/AutoFix/issues) or
Email <cuieney@163.com>

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
> ``

