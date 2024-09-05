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

    fun requestWXpay(@NonNull orderInfo: String,wxAppId:String?=null): Flowable<Boolean> {
        return wxPayment(orderInfo,wxAppId)
    }

    private fun ensure(payWay: PayWay, orderInfo: String,wxAppId:String?=null): FlowableTransformer<Any, Boolean> {
        return FlowableTransformer {
            requestImplementation(payWay,orderInfo,wxAppId).map { paymentStatus -> paymentStatus.isStatus }
        }
    }

    private fun requestImplementation(payWay: PayWay, orderInfo: String?,wxAppId:String?=null): Flowable<PaymentStatus> {
        if (payWay === PayWay.WECHATPAY) {
            return WXPayWay.payMoney(activity, orderInfo!!,wxAppId)

        } else if (payWay === PayWay.ALIPAY) {
            return AlipayWay.payMoney(activity, orderInfo!!)

        }
        throw IllegalArgumentException("This library just supported ali and wechat pay")
    }

    private fun aliPayment(orderInfo: String): Flowable<Boolean> {
        return Flowable.just(orderInfo).compose(ensure(PayWay.ALIPAY, orderInfo))
    }

    private fun wxPayment(orderInfo: String,wxAppId:String?): Flowable<Boolean> {
        return Flowable.just(orderInfo).compose(ensure(PayWay.WECHATPAY,orderInfo,wxAppId))
    }
//
//    companion object {
//        internal val TAG = "RxPay"
//    }
}