package com.cuieney.rxpay_master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cuieney.sdk.WX;
import com.cuieney.sdk.rxpay.RxPay;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@WX(packageName = "微信支付注册keystore时候的包名")
public class MainActivity extends AppCompatActivity {

    private View ali;
    private View wechat;
    private TextView payState;
    private RxPay rxPay;

    private String json = "服务器生成订单后的json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ali = findViewById(R.id.ali);
        wechat = findViewById(R.id.wechat);
        payState = ((TextView) findViewById(R.id.state));
        rxPay = new RxPay(this);

        ali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAlipay();
            }
        });

        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    requestWechatpay();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void  requestAlipay(){
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
    }

    private void requestWechatpay() throws JSONException {
        rxPay.requestWXpay(new JSONObject(json))
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
    }
}
