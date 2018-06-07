package com.cuieney.sdk.rxpay

import android.app.Activity


import com.cuieney.sdk.rxpay.alipay.AlipayWay
import com.cuieney.sdk.rxpay.wechatpay.WXPayWay

import org.json.JSONObject

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.annotations.NonNull

/**
 * Created by cuieney on 18/08/2017.
 */

class RxPay(@param:NonNull private val activity: Activity) {

    fun requestAlipay(@NonNull orderInfo: String): Flowable<Boolean> {
        return aliPayment(orderInfo)
    }

    fun requestWXpay(@NonNull orderInfo: String): Flowable<Boolean> {
        return wxPayment(orderInfo)
    }


    private fun ensure(payWay: PayWay, orderInfo: String): FlowableTransformer<Any, Boolean> {
        return FlowableTransformer {
            requestImplementation(payWay,orderInfo).map { paymentStatus -> paymentStatus.isStatus }
        }
    }

    private fun requestImplementation(payWay: PayWay, orderInfo: String?): Flowable<PaymentStatus> {
        if (payWay === PayWay.WECHATPAY) {
            return WXPayWay.payMoney(activity, orderInfo!!)

        } else if (payWay === PayWay.ALIPAY) {
            return AlipayWay.payMoney(activity, orderInfo!!)

        }
        throw IllegalArgumentException("This library just supported ali and wechat pay")
    }


    private fun aliPayment(orderInfo: String): Flowable<Boolean> {
        return Flowable.just(orderInfo).compose(ensure(PayWay.ALIPAY, orderInfo))
    }

    private fun wxPayment(orderInfo: String): Flowable<Boolean> {
        return Flowable.just(orderInfo).compose(ensure(PayWay.WECHATPAY,orderInfo))
    }

    companion object {
        internal val TAG = "RxPay"
    }

}
