package net.ocsoft

/**
 * int type extension
 */
fun Int.toString(base: Byte, width: Byte):String {
    var base0 = base
    if (base0 <= 1) {
        base0 = 10
    } else if (base > 16) {
        base0 = 16
    }
    val sign = if (this < 0) { -1 } else { 1 }
    val plusValue = this * sign
    var a = plusValue % base0
    var b = (plusValue - a) / base0
    val charNums = ArrayList<Char>()
    while (true) {
        var numChar: Char
        if (a < 10) {
            numChar = '0' + a 
        } else {
            numChar = 'a' + (a - 10)
        }
        charNums.add(0, numChar)
        if (b <= 0) {
            break
        } 
        a = b % base0
        b -= a
        b /= base0
    }
    val signCount = if (sign < 0) { 1 } else { 0 }
    while (charNums.size + signCount < width) {
       charNums.add(0, '0') 
    }
    if (sign < 0) {
        charNums.add(0, '-')
    }
    return charNums.joinToString("")
}

// vi: se ts=4 sw=4 et:
