![](https://github.com/Cuieney/RxPay/blob/master/img/logo.png)


## What's RxPay ?

**让支付从此简单下去，一键支付功能，支持支付宝支付，微信支付**

## 使用步骤
### step 1
#### Gradle

```
dependencies {
    	compile 'com.cuieney:rxpay-api:2.0.0'
    	annotationProcessor 'com.cuieney:rxpay-compiler:2.0.0'
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
 rxPay.requestWXpay(new JSONObject(“服务器生成订单的后拼接成下图这种json”))
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
{
    "appId": "wxbc9988d5283cf187",
    "nonceStr": "22508552d3fc22f867e33e6c56b30b16",
    "packageValue": "prepay_id=wx2017111615352310043bce630782561965",
    "partnerId": "1343162201",
    "prepayId": "wx2017111615352310043bce630782561965",
    "sign": "995be96b4ecaa972e2a8c5a20a7289df",
    "timeStamp": "1510817728"
}


```


[code sample](https://github.com/Cuieney/rxpay/blob/master/app/src/main/java/com/cuieney/rxpay_master/MainActivity.java)
#### Tips
* 如果你的项目中有之前集成了支付宝，请记得删除了alipaySdk-xxxxxxxx.jar，不然会冲突。
* 对于调起微信支付的json的字段也可以参考[微信官方](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5&pass_ticket=NlgmO52K8bxhFGF0ADjLk11K%2FrANUVHsE4lZ%2FMoJa4HcUD2MPqzYCuYgRZV64lmQ)

#### 问题
发现bug或好的建议欢迎 [issues](https://github.com/Cuieney/RxPay/issues) or
Email <cuieney@163.com>

#### 微信交流群
![](https://github.com/Cuieney/RxPay/blob/master/img/wechat.png)

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


