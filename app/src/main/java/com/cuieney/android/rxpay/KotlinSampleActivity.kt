package com.cuieney.android.rxpay

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cuieney.rxpay_annotation.WX
import com.cuieney.sdk.rxpay.RxPay
import org.json.JSONException
import org.json.JSONObject

@WX(packageName = "com.xxx.xxx")//微信支付注册keystore时候的包名
class KotlinSampleActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.ali).setOnClickListener {
            alipay()
        }
        findViewById<View>(R.id.wechat).setOnClickListener {
            wechatPay()
        }
    }


    fun alipay() {
        //服务器产生的订单信息
        var str = "partner=\"2088121059329235\"&seller_id=\"1993349866@qq.com\"&out_trade_no=\"XGJ_LIVE20171130142905-440402\"&subject=\"一对一收费单节\"&body=\"一对一收费单节\"&total_fee=\"0.01\"&notify_url=\"http://new.antwk.com/api/order/alipayNotify\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"1757281m\"&return_url=\"m.alipay.com\"&sign=\"vn%2Fw5wJAYSdP5rtQxumnAXPaaidyeVOluEoDlvS4axezmvfpoIHzwxj5pqNrJ5NMKq7NK8krHWBo8Z6jeTkFbCb2mvLbyBicAjDz02WyPOmKM%2F%2FGRfqfDlX4Q0T06PQmipNFVD3UPHrwPQbHG3eeWobqBFG0jcu%2FtnMZrsZvzso%3D\"&sign_type=\"RSA\""
        RxPay(this).requestAlipay(str)
                .subscribe({ aBoolean ->
                    Log.e("oye", "accept: " + aBoolean!!)
                }) { throwable ->
                    Log.e("oye", "accept: ", throwable)
                }

    }

    fun wechatPay() {
        //"服务器生成订单后的json 具体看README格式"
        var str = "{\"prepayId\":\"wx20171130142918877d249e440347896475\"}"
        try {
            RxPay(this).requestWXpay(
                    JSONObject(str))
                    .subscribe({ aBoolean ->
                        Log.e("oye", "accept: " + aBoolean!!)
                    }) { throwable ->
                        Log.e("oye", "accept: ", throwable)
                    }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}
