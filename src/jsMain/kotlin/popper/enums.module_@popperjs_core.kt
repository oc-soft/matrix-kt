@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package popper

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external var top: String /* "top" */

external var bottom: String /* "bottom" */

external var right: String /* "right" */

external var left: String /* "left" */

external var auto: String /* "auto" */

external var basePlacements: Array<dynamic /* Any */>

external var start: String /* "start" */

external var end: String /* "end" */

external var clippingParents: String /* "clippingParents" */

external var viewport: String /* "viewport" */

external var popper: String /* "popper" */

external var reference: String /* "reference" */

external var variationPlacements: Array<String /* "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end" */>

external var placements: Array<dynamic /* "auto" | "auto-start" | "auto-end" | Any | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end" */>

external var beforeRead: String /* "beforeRead" */

external var read: String /* "read" */

external var afterRead: String /* "afterRead" */

external var beforeMain: String /* "beforeMain" */

external var main: String /* "main" */

external var afterMain: String /* "afterMain" */

external var beforeWrite: String /* "beforeWrite" */

external var write: String /* "write" */

external var afterWrite: String /* "afterWrite" */

external var modifierPhases: Array<dynamic /* Any */>
