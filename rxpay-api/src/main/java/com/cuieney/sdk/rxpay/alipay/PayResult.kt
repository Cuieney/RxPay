package com.cuieney.sdk.rxpay.alipay

import android.text.TextUtils

/**
 * Created by cuieney on 18/08/2017.
 */

class PayResult(rawResult: Map<String, String>?) {
    /**
     * @return the resultStatus
     */
    var resultStatus: String? = null
        private set
    /**
     * @return the result
     */
    var result: String? = null
        private set
    /**
     * @return the memo
     */
    var memo: String? = null
        private set

    init {
        if (rawResult != null) {
            for (key in rawResult.keys) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult[key]
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult[key]
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult[key]
                }
            }
        }

    }

    override fun toString(): String {
        return ("resultStatus={" + resultStatus + "};memo={" + memo
                + "};result={" + result + "}")
    }
}
