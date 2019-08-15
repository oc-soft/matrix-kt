package net.ocsoft.mswp

/**
 * manage byte order
 */
class ByteOrder {
    companion object {
        /**
         * you get true if the software run on little endian cpu.
         */
        val isLittleEndian: Boolean 
            get() {
                val oneValue = 0x1   
                val oneMemory = ByteArray(4) {
                    i ->
                    ((oneValue shr (8 * i)) and 0xf).toByte()
                }
                return oneMemory[0].toInt() == 1
            }
        /**
         * convert short value from machine byte-order memory block.
         */
        fun decodeShort(byteMem : ByteArray): Short {
            val tmpByteMem = byteMem.copyOf(2)
            var shortVal : Int 
            val  indices = IntArray(2)
            if (isLittleEndian) {
                indices[0] = 1
                indices[1] = 0
            } else {
                indices[1] = 1
                indices[0] = 0
            }
            shortVal = tmpByteMem[indices[0]].toInt() shl 8
            shortVal = shortVal or tmpByteMem[indices[1]].toInt()
            val result = shortVal.toShort()
            return result
        }
    }
}
