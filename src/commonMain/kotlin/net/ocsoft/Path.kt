package net.ocsoft

import kotlin.collections.MutableList
import kotlin.collections.ArrayList
import kotlin.collections.List

/**
 * manage svg path2d element
 */
class Path {

    /**
     * class instance
     */
    companion object {
    
        /**
         * parse path string. 
         */
        fun parse(pathData: String, 
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            val st = Stream(pathData)
            var result = false
            result = parse0(st, handler, errorHandler)
            return result
        }       

        /**
         *  parse start
         */
        fun parse0(stream: Stream, 
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = parseWspZeroOrMore(stream)
            result = parseMoveto(stream, handler, errorHandler) 
            if (result) {
                do {
                    parseWspZeroOrMore(stream)
                    if (stream.peekChar == null) {
                        break
                    }
                    result = parseDrawto(stream, handler, errorHandler)
                } while(result)
            } else {
                result = true
            }
            return result
        }

        /**
         * parse draw to
         */
        fun parseDrawto(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            
            var parseMethods : Array<(
                Stream, (Element)->Boolean, ((Int)->Unit)?)->Boolean>
            = arrayOf(
                ::parseMoveto,
                ::parseLineto,
                ::parseHorizontalLineto,
                ::parseVerticalLineto,
                ::parseCurveto,
                ::parseSmoothCurveto,
                ::parseQuadraticBezierCurveto,
                ::parseSmoothQuadraticBezierCurveto,
                ::parseEllipticalArc,
                ::parseBearing,
                ::parseCatmullRom
            ) 
            var result = false
            parseMethods.find { 
                result = it(stream, handler, errorHandler)
                result
            }
            return result
        }

        /**
         * parse moveTo
         */
        fun parseMoveto(stream: Stream, 
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar  
            if ('M' == pc || 'm' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinates = ArrayList<Pair<Int, Int>>()
                result = parseCoordinatePairSequence(stream,
                    coordinates)
                if (result) {
                    var elemType = ElementType.valueOf(pc.toString())  
                    val flatCoordinates = ArrayList<Int>()
                    coordinates.forEach { 
                        flatCoordinates.addAll(it.toList()) 
                    } 
                    result = handler(Element(elemType, flatCoordinates))
                    if (result) {
                        parseClosepath(stream, handler, null)
                    }
                } else {
                    if (errorHandler != null) {
                        errorHandler(stream.index)
                    }
                }
            }
            return result
        }

        /**
         * parse close path
         */
        fun parseClosepath(stream: Stream,
            handler: ((Element)->Boolean),
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar 
            if ('Z' == pc || 'z' == pc) {
                result = handler(
                    Element(ElementType.valueOf(pc.toString()),
                        ArrayList<Int>())) 
                stream.nextChar
            }
            return result
        }
        
        /**
         * parse
         */
        fun parseLineto(stream: Stream,
            handler: ((Element)->Boolean),
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar
            if ('L' == pc || 'l' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinates = ArrayList<Pair<Int, Int>>()
                result = parseCoordinatePairSequence(stream,
                    coordinates)
                val flatCoordinates = ArrayList<Int>()
                coordinates.forEach {
                    flatCoordinates.addAll(it.toList())
                } 
                val elem = Element(ElementType.valueOf(pc.toString()),
                        flatCoordinates)
                if (result) {
                    result = handler(elem)
                } else {
                    var closepathRes: Element? = null
                    result = parseClosepath(stream, { 
                        closepathRes = it 
                        true
                    }, null)
                    if (result) {
                        result = handler(elem)
                    }
                    if (result) {
                        result = handler(closepathRes!!)
                    } else {
                        if (errorHandler != null) {
                            errorHandler(stream.index)
                        }
                    }
                }
            }
            return result
        }

        /**
         * parse horizontal lineto
         */
        fun parseHorizontalLineto(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar
            if ('H' == pc || 'h' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinates = ArrayList<Int>()
                result = parseCoordinateSequence(stream, coordinates)
                if (result) {
                    result = handler(Element(
                        ElementType.valueOf(pc.toString()),
                        coordinates))
                } else {
                    if (errorHandler != null) {
                        errorHandler(stream.index)
                    }
                }
            }
            return result
        }

        /**
         * parse vertical lineto
         */
        fun parseVerticalLineto(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar
            if ('H' == pc || 'h' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinates = ArrayList<Int>()
                result = parseCoordinateSequence(stream,coordinates)
                if (result) {
                    result = handler(Element(
                        ElementType.valueOf(pc.toString()),
                        coordinates))
                } else {
                    if (errorHandler != null) {
                        errorHandler(stream.index)
                    } 
                }
            }
            return result
        }

        /**
         * parse curveto
         */
        fun parseCurveto(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar
            if ('C'== pc || 'c' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinates = ArrayList<Int>()
                val coordinatePairs = ArrayList<Pair<Int, Int>>()
                result = parseCurvetoCoordinateSequence(stream,
                    coordinatePairs)
                val elemType = ElementType.valueOf(pc.toString())
                if (result) {
                    coordinatePairs.forEach {
                        coordinates.addAll(it.toList())
                    }
                    result = handler(Element(elemType, coordinates))
                } else {
                    parseCoordinatePairSequence(stream, coordinatePairs)
                    coordinatePairs.forEach {
                        coordinates.addAll(it.toList())
                    }
                    var closepathRes: Element? = null
                    result = parseClosepath(stream, { 
                        closepathRes = it
                        true
                    }, null)
                    if (result) {
                        result = handler(Element(elemType, coordinates))
                    }
                    if (result) {
                        result = handler(closepathRes!!)
                    } else {
                        if (errorHandler != null) {
                            errorHandler(stream.index)
                        }
                    }
                }
            }
            return result
        } 
        
        /**
         * parse curveto coorditate sequence
         */
        fun parseCurvetoCoordinateSequence(stream: Stream,
            coordinates: MutableList<Pair<Int, Int>>?): Boolean {
            var result = false
             
            result = parseCoordinatePairTriplet(stream, coordinates)
            if (result) {
                val savedIdx = stream.index
                parseCommaWsp(stream)
                result = parseCurvetoCoordinateSequence(stream, 
                    coordinates)
                if (!result) {
                    result = true
                    stream.index = savedIdx
                }
            }
            return result
        }
        /**
         * parse smooth curveto
         */
        fun parseSmoothCurveto(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar
            if ('S'== pc || 's' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinatePairs = ArrayList<Pair<Int, Int>>()
                result = parseSmoothCurvetoCoordinateSequence(
                    stream, coordinatePairs)
                if (result) {
                    val coordinates = ArrayList<Int>()
                    coordinatePairs.forEach {
                        coordinates.addAll(it.toList())
                    }
                    result = handler(
                        Element(ElementType.valueOf(pc.toString()), 
                            coordinates))
                } else {
                    parseCoordinatePairSequence(stream, 
                        coordinatePairs)
                    val coordinates = ArrayList<Int>()
                    coordinatePairs.forEach {
                        coordinates.addAll(it.toList())
                    }
 
                    var closepathRef: Element? = null 
                    result = parseClosepath(stream, {
                        closepathRef = it
                        true
                    }, null)
                    if (result) {
                        result = handler(Element(
                            ElementType.valueOf(pc.toString()),
                            coordinates))
                        if (result) {
                            result = handler(closepathRef!!)
                        }
                    } else {
                        if (errorHandler != null) {
                            errorHandler(stream.index)
                        }
                    }
                }
            } 
            return result
        }
        /**
         * parse smooth curveto coordinate sequence
         */
        fun parseSmoothCurvetoCoordinateSequence(
            stream: Stream,
            coordinates: MutableList<Pair<Int, Int>>?): Boolean {
            var result = false
            result = parseCoordinatePairDouble(stream, coordinates) 
            if (result) {
                parseCommaWsp(stream)
                parseSmoothCurvetoCoordinateSequence(stream, coordinates)
            } 
            return result
        }

        /**
         * parse quadratic bezier curve
         */
        fun parseQuadraticBezierCurveto(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            val pc = stream.peekChar
            if ('Q' == pc || 'q' == pc) {
                stream.nextChar
                val coordinatePairs = ArrayList<Pair<Int, Int>>()
                parseCommaWsp(stream)
                result = parseQuadraticBezierCurvetoCoordinateSequence(
                    stream, 
                    coordinatePairs)
                
                if (result) {
                    val coordinates = ArrayList<Int>()
                    coordinatePairs.forEach {
                        coordinates.addAll(it.toList())
                    }
                    result = handler(
                        Element(ElementType.valueOf(pc.toString()),
                        coordinates))
                } else {
                    parseCoordinatePairSequence(
                        stream, coordinatePairs) 
                    var closepathElem: Element? = null
                    result = parseClosepath(stream, {
                        closepathElem = it
                        true
                    }, null)
                    if (result) {
                        val coordinates = ArrayList<Int>()
                        coordinatePairs.forEach {
                            coordinates.addAll(it.toList())
                        }
 
                        result = handler(Element(
                            ElementType.valueOf(pc.toString()),
                            coordinates))
                        if (result) {
                            handler(closepathElem!!)
                        }
                    } else {
                        if (errorHandler != null) {
                            errorHandler(stream.index)
                        }
                    } 
                }
            } 
            return result
        } 

        /**
         * parse quadratic curveto coordinate sequence
         */
        fun parseQuadraticBezierCurvetoCoordinateSequence(
            stream: Stream,
            coordinates: MutableList<Pair<Int, Int>>?): Boolean {
            var result = false
            result = parseCoordinatePairDouble(stream, coordinates)
            if (result) {
                parseWsp(stream)
                parseQuadraticBezierCurvetoCoordinateSequence(stream,
                    coordinates)
            } 
            return result
        }

        /**
         * smooth quadratic bezier curveto
         */
        fun parseSmoothQuadraticBezierCurveto(
            stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            var pc = stream.nextChar
            if ('T' == pc || 't' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinatePairs = ArrayList<Pair<Int, Int>>()
                result = parseCoordinatePairSequence(stream,
                    coordinatePairs)
                if (result) {
                    val coordinates = ArrayList<Int>()
                    coordinatePairs.forEach {
                        coordinates.addAll(it.toList())
                    } 
                    result = handler(
                        Element(ElementType.valueOf(pc.toString()),
                            coordinates)) 
                } else {
                    if (errorHandler != null) {
                        errorHandler(stream.index)
                    }
                }
            }
            return result
        }

        /**
         * elliptical arc
         */
        fun parseEllipticalArc(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var result = false
            var pc = stream.peekChar
            if ('A' == pc || 'a' == pc) {
                stream.nextChar
                val args = ArrayList<Int>()
                result = parseEllipticalArcArgumentSequence(stream, args) 
                var closingRes: Element? = null
                val savedIndex = stream.index
                parseCommaWsp(stream)
                var closingArgPathRes = false
                val closingArgs = ArrayList<Int>()
                closingArgPathRes = parseEllipticalArcClosingArgument(
                    stream, closingArgs) {
                        closingRes = it
                        true
                    }
                if (!result && !closingArgPathRes) {
                    stream.index = savedIndex
                }
                val elemType = ElementType.valueOf(pc.toString())
                if (result && closingRes == null) {
                    result = handler(
                        Element(elemType, args))
                } else if (closingArgPathRes) {
                    args.addAll(closingArgs)
                    result = handler(Element(elemType, args))
                    if (result) {
                        result = handler(closingRes!!)
                    } 
                } else {
                    if (errorHandler != null) {
                        errorHandler(stream.index)
                    }
                } 
            }
            return result
        }

        /**
         * parse elliptical arc argument sequence
         */
        fun parseEllipticalArcArgumentSequence(stream: Stream,
            ellipticalArgs: MutableList<Int>?): Boolean {
            var result = parseEllipticalArcArgument(stream,
                ellipticalArgs)
            if (result) {
                parseCommaWsp(stream)
                parseEllipticalArcArgumentSequence(stream,
                    ellipticalArgs)
            }
            return result
        }
        /**
         * parse elliptical arc argument
         */
        fun parseEllipticalArcArgument(stream: Stream,
            ellipticalArcArgument: MutableList<Int>?): Boolean {
            var nums = IntArray(7)
            var result = parseNumber(stream, { nums[0] = it })
            if (result) {
                parseCommaWsp(stream)
                result = parseNumber(stream, { nums[1] = it })
            }
            if (result) {
                parseCommaWsp(stream)
                result = parseNumber(stream, { nums[2] = it })
            }
            if (result) {
                result = parseCommaWsp(stream)
            }
            if (result) {
                result = parseFlag(stream, { nums[3] = it })
            } 
            if (result) {
                parseCommaWsp(stream)
                result = parseFlag(stream, { nums[4] = it })
            }
            if (result) {
                parseCommaWsp(stream)
                result = parseCoordinatePair(stream) {
                    nums[5] = it.first
                    nums[6] = it.second
                }
            }
            if (result) {
                nums.forEach {
                    ellipticalArcArgument?.add(it)
                }
            }
            
            return result
        }
        
        /**
         * parse elliptical arc closing argument
         */ 
        fun parseEllipticalArcClosingArgument(
            stream: Stream,
            ellipticalArcArgs: MutableList<Int>?,
            closepathHdlr: (Element)->Boolean): Boolean {
            val nums = IntArray(5)
            var result = parseNumber(stream, { nums[0] = it })
            if (result) {
                parseCommaWsp(stream)
                result = parseNumber(stream, { nums[1] = it })
            }
            if (result) {
                parseCommaWsp(stream)
                result = parseNumber(stream, { nums[2]  = it })
            }
            if (result) {
                result = parseCommaWsp(stream)
            }
            if (result) {
                result = parseFlag(stream, { nums[3] = it })
            }
            if (result) {
                parseCommaWsp(stream)
                result = parseFlag(stream, { nums[4] = it })
            }
            var closepathRes: Element? = null
            if (result) {
                parseCommaWsp(stream)
                result = parseClosepath(stream, {
                    closepathRes = it
                    true 
                }, null)
            }
            if (result) {
                nums.forEach {
                     ellipticalArcArgs?.add(it)
                }
                result = closepathHdlr(closepathRes!!)
            }
            return result
        }


        /**
         * parse bearing
         */
        fun parseBearing(
            stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var pc = stream.peekChar
            var result = false
            if ('B' == pc || 'b' == pc) {
                parseWspZeroOrMore(stream)
                val args = ArrayList<Int>() 
                result = parseBearingArgumentSequence(stream, args) 
                if (result) {
                    result = handler(
                        Element(
                            ElementType.valueOf(pc.toString()), args))
                } else {
                    if (errorHandler != null) {
                        errorHandler(stream.index)
                    }
                }
            }
            return result 
        }

        /**
         * parse bearing argument sequence
         */
        fun parseBearingArgumentSequence(
            stream: Stream,
            bearingArgs: MutableList<Int>?): Boolean {
            var result = false
            var num: Int = 0 
            result = parseNumber(stream, { num = it })
            if (result) {
                bearingArgs?.add(num)
                parseBearingArgumentSequence(stream, bearingArgs)
            }
            
            return result
        }

        /**
         * parse catmull-rom 
         */
        fun parseCatmullRom(stream: Stream,
            handler: (Element)->Boolean,
            errorHandler: ((Int)->Unit)?): Boolean {
            var pc = stream.peekChar
            var result = false
            if ('R' == pc || 'r' == pc) {
                parseWspZeroOrMore(stream)
                val pairArgs = ArrayList<Pair<Int, Int>>()
                result =  parseCatmullRomArgumentSequence(stream, 
                    pairArgs) 
                if (result) {
                    val args = ArrayList<Int>()
                    pairArgs.forEach {
                        args.addAll(it.toList())
                    }
                    result = handler(Element(
                        ElementType.valueOf(pc.toString()),
                        args))
                } else {
                    if (errorHandler != null) {
                        errorHandler(stream.index)
                    }
                }
            }
            return result
        }

        /**
         * parse catmull-rom  arugment sequence
         */
        fun parseCatmullRomArgumentSequence(
            stream: Stream,
            catmullArgs: MutableList<Pair<Int, Int>>?): Boolean {
            val tmpArgs = ArrayList<Pair<Int, Int>>()
            var result = parseCoordinatePair(stream) {
                tmpArgs.add(it)
            }
            if (result) {
                result = parseCoordinatePair(stream) {
                    tmpArgs.add(it)
                }
            } 
            if (result) {
                result = parseCoordinatePair(stream) {
                    tmpArgs.add(it)
                }
            }
            if (result) {
                catmullArgs?.addAll(tmpArgs)
                var state = true
                do {
                    state = parseCoordinatePair(stream) {
                        catmullArgs?.add(it)
                    }
                } while (state)
                 
            }
            return result
        }
        /**
         * parse coorditate pair double
         */
        fun parseCoordinatePairDouble(stream: Stream,
            coordinatePairs: MutableList<Pair<Int, Int>>?): Boolean {
            
            val coordinates = ArrayList<Pair<Int, Int>>()
            var result = false
            result = parseCoordinatePair(stream) { coordinates.add(it) }
            if (result) {
                parseCommaWsp(stream)
                result = parseCoordinatePair(stream) {
                    coordinates.add(it)
                }
                if (result) {
                    coordinatePairs?.addAll(coordinates)
                }
            }
            return result
        }
        
        /**
         * parse coorditate pair triplet
         */
        fun parseCoordinatePairTriplet(stream: Stream,
            coordinatePairs: MutableList<Pair<Int, Int>>?): Boolean {
            val pairList = ArrayList<Pair<Int, Int>>() 
            var result = false
            result = parseCoordinatePair(stream, { pairList.add(it) })
            if (result) {
                for (i in 0..1) {
                    parseCommaWsp(stream)
                    result = parseCoordinatePair(stream) {
                        pairList.add(it)
                    }
                    if (!result) {
                        break
                    }
                }
            }
            if (result) {
                coordinatePairs?.addAll(pairList)
            }
            return result
        }

        /**
         * parse coordinate pair sequence
         */
        fun parseCoordinatePairSequence(
            stream: Stream,
            coordinates: MutableList<Pair<Int, Int>>?): Boolean {
            var result = parseCoordinatePair(stream) { 
                coordinates?.add(it)
            }
            if (result) {
                parseCommaWsp(stream)
                parseCoordinatePairSequence(stream, coordinates)
            }
            return result
        }

        /**
         * parse coordinate sequence
         */
        fun parseCoordinateSequence(stream: Stream,
            coordinates: MutableList<Int>?): Boolean {
            var result = parseCoordinate(stream) {
                coordinates?.add(it)
            }

            if (result) {
                parseCommaWsp(stream)
                parseCoordinateSequence(stream, coordinates)
            }
            return result
        }


        /**
         * parse coordinate pair
         */
        fun parseCoordinatePair(stream: Stream,
            handler : ((Pair<Int, Int>)->Unit)?): Boolean {
            val numbers = intArrayOf(0, 0)
            var result = parseCoordinate(stream, { numbers[0] = it })
            if (result) {
                parseCommaWsp(stream)
                result = parseCoordinate(stream, { numbers[1] = it })
                if (result) {
                    if (handler != null) {
                        handler(Pair<Int, Int>(numbers[0], numbers[1]))
                    }
                }
            }
            return result
        }

        /**
         * parse coordinate
         */
        fun parseCoordinate(stream: Stream, 
            handler: ((Int)->Unit)?): Boolean {
            var result = false
            var sign = 1
            parseSign(stream, { sign = it })
            var number: Int = 0 
            result = parseNumber(stream, { number = it })
            if (result) { 
                if (handler != null ) {
                    handler(sign * number) 
                }  
            }
            
            return result
        }
        /**
         * parse sign
         */
        fun parseSign(stream: Stream,
            handler: ((Int)->Unit)?): Boolean {
            var result = false
            var pc = stream.peekChar
            var sign = 1
            if ('+' == pc) {
                result = true
            }
            if ('-' == pc) {
                result = true
                sign = -1
            }
            if (result) {
                if (handler != null) {
                    handler(sign)
                }
                stream.nextChar
            } 
            return result
        }

        /**
         * parse number
         */
        fun parseNumber(stream: Stream,
            handler: ((Int)->Unit)?): Boolean {
            var result = false 
            var hit = false
            var number = 0 
            do {
                var pc = stream.peekChar
                if (pc != null) {
                    hit = '0' <= pc && pc <= '9'
                } else {
                    hit = false
                }
                if (hit) {
                    result = true
                    number *= 10
                    number += (pc!! - '0') 
                    stream.nextChar
                }
            } while (hit)
            if (result && handler != null) {
                handler(number)
            } 
            return result
        }
        
        /**
         * parse flag
         */
        fun parseFlag(stream: Stream,
            handler: (Int)->Unit): Boolean {
            var result = false
            var pc = stream.peekChar
            result = '0' == pc || '1' == pc
            if (result) {
                handler(pc!! - '0')
                stream.nextChar
            }
            return result
        }

        /**
         * parse comma and white space
         */
        fun parseCommaWsp(stream: Stream): Boolean {
            var result = false
            result = parseWsp(stream)
            if (result) {
                var pc = stream.peekChar
                if (',' == pc) {
                    stream.nextChar
                }
                while (parseWsp(stream)) {
                }
            } else {
                var pc = stream.peekChar
                if (',' == pc) {
                    result = true
                    stream.nextChar
                    while(parseWsp(stream)) {
                    }
                }
            } 
            return result 
        }

        /**
         * parse white space
         */
        fun parseWsp(stream: Stream): Boolean {
            var result = false
            var pc = stream.peekChar 
            if (pc != null) {
                val aChar = pc!!
                if ('\u0009' == aChar
                    || '\u0020' == aChar
                    || '\u000A' == aChar 
                    || '\u000C' == aChar 
                    || '\u000D' == aChar) {
                    result = true
                    stream.nextChar
                }
            }
            return result
        }
 
        /**
         * parse white space zero or more
         */
        fun parseWspZeroOrMore(stream: Stream): Boolean {
            var result = parseWsp(stream)
            if (result) {
                parseWspZeroOrMore(stream)
            }
            return result
        }

        
    }

    /**
     * path string stream 
     */
    class Stream(val pathString: String) {
        
        /**
         * current index
         */
        var index: Int = 0 
       
        /**
         * peek character
         */
        val peekChar: Char?
            get() {
                var result: Char? = null
                if (index < pathString.length) {
                    result = pathString[index] 
                }
                return result
            }
        /**
         * next character
         */
        val nextChar: Char?
            get() {
                var result = peekChar
                if (result != null) {
                    index++
                }
                return result
            }
   }


    /**
     * element type
     */
    enum class ElementType {
        /**
         * absolute move to
         */
        M,
        /**
         * relative move to
         */
        m,
        /**
         * absolute line to
         */
        L,
        /**
         * relative line to
         */
        l,
        /**
         * close path
         */
        Z,
        /**
         * holizontal line to absolute
         */
        H,
        /**
         * holizontal line to relative
         */
        h,
        /**
         *  vertical line to absolute
         */
        V,
        /**
         * vertical line to relative
         */
        v,
        /**
         * curve to absolute
         */
        C,
        /**
         * curve to relative
         */
        c,
        /**
         * shorthand curve to absolute
         */
        S,
        /**
         * shorthand curve to relative
         */
        s,
        /**
         * quadric bezier curve to absolute
         */
        Q,
        /**
         * quadric bezier curve to relative
         */
        q,
        /**
         * shorthand quadric bezier curve to absolute
         */
        T,
        /**
         * shorthand quadric bezier curve to relative
         */
        t,
        /**
         * elliptical arc curve absolute
         */
        A,
        /**
         * elliptical arc curve relative
         */
        a,
        /**
         * catmall-rom curve absolute
         */
        R,
        /**
         * catmall-rom curve relative
         */
        r,
        /**
         * Bearing absolute
         */ 
        B,
        /**
         * bearing relative
         */
        b
    }

    /**
     * path element
     */
    data class Element(val type: ElementType, val data: List<Int>) 
    
}
