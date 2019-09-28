package net.ocsoft.mswp

import kotlin.browser.window
import kotlin.js.Promise

/**
 * manage activity
 */
class Activity {

    /**
     * class instance
     */
    companion object {
        /**
         * record activity
         */
        fun record(): Promise<Any?> {
            val result = Promise<Any?>({
                resolve, reject ->
                window.fetch("activity.php").then(
                    { res -> res.json() }).then(
                    { res -> resolve(res) }).catch(
                    { reason -> reject(Throwable(reason)) })
            })
            return result
        }
    }
}

