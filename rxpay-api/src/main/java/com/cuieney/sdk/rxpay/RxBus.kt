package com.cuieney.sdk.rxpay

import io.reactivex.Flowable
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor

/**
 * Help the callback act and rx communication
 */
class RxBus private constructor() {
    private val mBus: FlowableProcessor<Any>

    init {
        // toSerialized method made bus thread safe
        mBus = PublishProcessor.create()
    }

    fun post(obj: Any) {
        mBus.onNext(obj)
    }

    fun <T> toFlowable(tClass: Class<T>): Flowable<T> {
        return mBus.ofType(tClass)
    }

    fun toFlowable(): Flowable<Any> {
        return mBus
    }

    fun hasSubscribers(): Boolean {
        return mBus.hasSubscribers()
    }

    private object Holder {
        val BUS = RxBus()
    }

    companion object {

        val default: RxBus
            get() = Holder.BUS
    }
}
