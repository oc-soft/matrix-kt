package net.ocsoft

import kotlin.collections.MutableList
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.math.*

import net.ocsoft.Matrix2

/**
 * extention
 */
operator fun Pair<Double, Double>.plus(
    other: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(this.first + other.first, this.second + other.second) 
}

/**
 * extention
 */
operator fun Pair<Double, Double>.minus(
    other: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(first - other.first, second - other.second) 
}




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
                ::parseClosepath,
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
                val coordinates = ArrayList<Pair<Double, Double>>()
                result = parseCoordinatePairSequence(stream,
                    coordinates)
                if (result) {
                    var elemType = ElementType.valueOf(pc.toString())  
                    val flatCoordinates = ArrayList<Double>()
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
                    Element(ElementType.Z,
                        ArrayList<Double>())) 
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
                val coordinates = ArrayList<Pair<Double, Double>>()
                result = parseCoordinatePairSequence(stream,
                    coordinates)
                val flatCoordinates = ArrayList<Double>()
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
                val coordinates = ArrayList<Double>()
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
            if ('V' == pc || 'v' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinates = ArrayList<Double>()
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
                val coordinates = ArrayList<Double>()
                val coordinatePairs = ArrayList<Pair<Double, Double>>()
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
            coordinates: MutableList<Pair<Double, Double>>?): Boolean {
            var result = false
             
            result = parseCoordinatePairTriplet(stream, coordinates)
            if (result) {
                val savedIdx = stream.index
                parseCommaWsp(stream)
                val parseRes0 = parseCurvetoCoordinateSequence(stream, 
                    coordinates)
                if (!parseRes0) {
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
                val coordinatePairs = ArrayList<Pair<Double, Double>>()
                result = parseSmoothCurvetoCoordinateSequence(
                    stream, coordinatePairs)
                if (result) {
                    val coordinates = ArrayList<Double>()
                    coordinatePairs.forEach {
                        coordinates.addAll(it.toList())
                    }
                    result = handler(
                        Element(ElementType.valueOf(pc.toString()), 
                            coordinates))
                } else {
                    parseCoordinatePairSequence(stream, 
                        coordinatePairs)
                    val coordinates = ArrayList<Double>()
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
            coordinates: MutableList<Pair<Double, Double>>?): Boolean {
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
                val coordinatePairs = ArrayList<Pair<Double, Double>>()
                parseCommaWsp(stream)
                result = parseQuadraticBezierCurvetoCoordinateSequence(
                    stream, 
                    coordinatePairs)
                
                if (result) {
                    val coordinates = ArrayList<Double>()
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
                        val coordinates = ArrayList<Double>()
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
            coordinates: MutableList<Pair<Double, Double>>?): Boolean {
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
            var pc = stream.peekChar
            if ('T' == pc || 't' == pc) {
                stream.nextChar
                parseWspZeroOrMore(stream)
                val coordinatePairs = ArrayList<Pair<Double, Double>>()
                result = parseCoordinatePairSequence(stream,
                    coordinatePairs)
                if (result) {
                    val coordinates = ArrayList<Double>()
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
                parseWspZeroOrMore(stream)
                val args = ArrayList<Double>()
                result = parseEllipticalArcArgumentSequence(stream, args) 
                var closingRes: Element? = null
                val savedIndex = stream.index
                parseCommaWsp(stream)
                var closingArgPathRes = false
                val closingArgs = ArrayList<Double>()
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
            ellipticalArgs: MutableList<Double>?): Boolean {
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
            ellipticalArcArgument: MutableList<Double>?): Boolean {
            var nums = DoubleArray(7)
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
            ellipticalArcArgs: MutableList<Double>?,
            closepathHdlr: (Element)->Boolean): Boolean {
            val nums = DoubleArray(5)
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
                val args = ArrayList<Double>() 
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
            bearingArgs: MutableList<Double>?): Boolean {
            var result = false
            var num = 0.0  
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
                val pairArgs = ArrayList<Pair<Double, Double>>()
                result =  parseCatmullRomArgumentSequence(stream, 
                    pairArgs) 
                if (result) {
                    val args = ArrayList<Double>()
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
            catmullArgs: MutableList<Pair<Double, Double>>?): Boolean {
            val tmpArgs = ArrayList<Pair<Double, Double>>()
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
            coordinatePairs: MutableList<Pair<Double, Double>>?): Boolean {
            
            val coordinates = ArrayList<Pair<Double, Double>>()
            var result = false
            val savedIndex = stream.index
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
            if (!result) {
                stream.index = savedIndex
            }
            return result
        }
        
        /**
         * parse coorditate pair triplet
         */
        fun parseCoordinatePairTriplet(stream: Stream,
            coordinatePairs: MutableList<Pair<Double, Double>>?): Boolean {
            val pairList = ArrayList<Pair<Double, Double>>() 
            var result = false
            val savedIndex = stream.index
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
            } else {
                stream.index = savedIndex
            }
            return result
        }

        /**
         * parse coordinate pair sequence
         */
        fun parseCoordinatePairSequence(
            stream: Stream,
            coordinates: MutableList<Pair<Double, Double>>?): Boolean {
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
            coordinates: MutableList<Double>?): Boolean {
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
            handler : ((Pair<Double, Double>)->Unit)?): Boolean {
            val numbers = doubleArrayOf(0.0, 0.0)
            var result = parseCoordinate(stream, { numbers[0] = it })
            if (result) {
                parseCommaWsp(stream)
                result = parseCoordinate(stream, { numbers[1] = it })
                if (result) {
                    if (handler != null) {
                        handler(Pair<Double, Double>(numbers[0], numbers[1]))
                    }
                }
            }
            return result
        }

        /**
         * parse coordinate
         */
        fun parseCoordinate(stream: Stream, 
            handler: ((Double)->Unit)?): Boolean {
            var result = false
            result = parseNumber(stream) { 
                if (handler != null) {
                    handler(it)
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
            handler: ((Double)->Unit)?): Boolean {
            var sign = 1
            parseSign(stream) { sign = it }
            var number = 0.0
            val numberHandlerF = {
                num: Double -> 
                number = sign.toDouble() * num
            }
            val numberHandlerI = {
                num: Int ->
                number = sign.toDouble() * num.toDouble()
            }
            var result = parseFloatingPointConstant(stream, numberHandlerF)
            if (!result) {
                result = parseIntegerConstant(stream, numberHandlerI)
            }
            if (result) {
                if (handler != null) {
                    handler(number)
                }
            }
            return result
        }

        /**
         * parse Integer 
         */
        fun parseIntegerConstant(stream: Stream,
            handler: ((Int)->Unit)?): Boolean {
            return parseDigitSequence(stream, handler)
        }

        /**
         * parse digit sequens as integer
         */
        fun parseDigitSequence(stream: Stream,
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
         * parse floating point constant
         */ 
        fun parseFloatingPointConstant(
            stream: Stream,
            handler: ((Double)->Unit)?): Boolean {
            
            var intPart = 0 
            var fractionalPart = 0
            var exponentNum = 0
            var result = parseFractionalConstant(stream) { 
                dig1, dig2 ->
                if (dig1 != null) {
                    intPart = dig1  
                }
                if (dig2 != null) {
                    fractionalPart = dig2 
                }
            }
            if (result) {
                parseExponent(stream) {
                    exponentNum = it
                }
            } else {
                val savedIndex = stream.index
                result = parseDigitSequence(stream) {
                    intPart = it
                }
                if (result) {
                    result = parseExponent(stream) {
                        exponentNum = it
                    }
                }
                if (!result) {
                    stream.index = savedIndex
                }
            }
            if (result) {
                var fractionalNum = 0.0
                if (fractionalPart > 0) {
                    var expNum = 0.0
                    var fracPartF =  fractionalPart.toDouble()
                    expNum = log10(fracPartF + 1)
                    expNum = ceil(expNum)
                    fractionalNum = fracPartF * 10.0.pow(-expNum)
                }
                fractionalNum += intPart.toDouble()
                fractionalNum *= 10.0.pow(exponentNum) 
                if (handler != null) {
                    handler(fractionalNum)
                } 
            }
            return result
        }
       
        /**
         * parse flactional data
         */
        fun parseFractionalConstant(
            stream: Stream,
            handler: ((Int?, Int?)->Unit)?): Boolean {
            var digit1: Int? = null   
            var digit2: Int? = null
            var result = false
            val savedIndex = stream.index  
            parseDigitSequence(stream, { digit1 = it } )
            if ('.' == stream.peekChar) {
                stream.nextChar
                result = parseDigitSequence(stream, { digit2 = it })
                if (digit1 != null) {
                    result = true
                }
                if (result) {
                    if (handler != null) {
                        handler(digit1, digit2)
                    }
                } 
            }
            if (!result) {
                stream.index = savedIndex 
            }
            return result
        } 
        /**
         * parse exponent
         */
        fun parseExponent(stream :Stream,
            handler: ((Int)->Unit)?): Boolean {
            var result = 'e' == stream.peekChar
                || 'E' == stream.peekChar 
            if (result) {
                val savedIndex = stream.index  
                var sign = 1
                stream.nextChar
                parseSign(stream) { sign = it }
                result = parseDigitSequence(stream) {
                    if (handler != null) {
                        handler(sign * it)
                    }
                } 
                if (!result) {
                    stream.index = savedIndex
                }  
            }
            return result
        }
        /**
         * parse flag
         */
        fun parseFlag(stream: Stream,
            handler: (Double)->Unit): Boolean {
            var result = false
            var pc = stream.peekChar
            result = '0' == pc || '1' == pc
            if (result) {
                val iVal = pc!! - '0'
                handler(iVal.toDouble())
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
        /**
         * convert from svg arc parameter to path2d arc parameter
         */
        fun resolveArcParam(radii: Pair<Double, Double>,
            xAxisRotation: Double,
            largeArcFlag: Int,
            sweepFlag: Int,
            point1: Pair<Double, Double>,
            point2: Pair<Double, Double>): EllipseParam? {
            var result: EllipseParam? = null

            if (radii.first != 0.0 && radii.second != 0.0) {
                val radii2 = Pair(abs(radii.first), abs(radii.second)) 
                result = resolveArcParami(radii2,
                    xAxisRotation, 
					largeArcFlag != 0,
                    sweepFlag != 0,
					point1, point2)

            }
            return result
        }
 

        /**
         * convert from svg arc parameter to path2d arc parameter
         */
        fun resolveArcParami(radii: Pair<Double, Double>,
            xAxisRotation: Double,
            largeArcFlag: Boolean,
            sweepFlag: Boolean,
            point1: Pair<Double, Double>,
            point2: Pair<Double, Double>): EllipseParam {

            val cosPhai = cos(xAxisRotation)
            val sinPhai = sin(xAxisRotation)
            // (x1', y1')
            val vecp1Src = doubleArrayOf((point1.first - point2.first) / 2,
                (point1.second - point2.second) / 2)

            val phaiRevMat = Matrix2(cosPhai, sinPhai, -sinPhai, cosPhai) 
            val p1dVec = phaiRevMat * vecp1Src 
            val p1d = Pair(p1dVec[0], p1dVec[1])
            val xdsq = p1d.first.pow(2)
            val ydsq = p1d.second.pow(2)
 
            var lambda = xdsq / radii.first.pow(2)
            lambda += ydsq / radii.second.pow(2)
            var radii2 = radii
            var rc = 0.0
            if (lambda > 1) {
                val sqrLambda = sqrt(lambda)
                radii2 = Pair(sqrLambda * radii.first, 
                    sqrLambda * radii.second) 
                
            } else {
                val rxsq = radii2.first.pow(2) 
                val rysq = radii2.second.pow(2)
                val nom1 = max(rxsq * rysq - rxsq * ydsq - rysq * xdsq, 0.0)
                val denom1 = rxsq * ydsq + rysq * xdsq
                rc = sqrt(nom1 / denom1)
            } 
           
            var cdsign = 1
            if (largeArcFlag == sweepFlag) {
                cdsign = -1
            }
            // (cx', cy')
            var cd = Pair(
                cdsign * rc * ((radii2.first * p1d.second) / radii2.second),
                - cdsign * rc * ((radii2.second * p1d.first) / radii2.first))

           
            // (x1', y1') - (cx', cy') vector 
            val pd1u = Pair((p1d.first - cd.first) / radii2.first, 
                (p1d.second - cd.second) / radii2.second)

            val pd2u = Pair((-p1d.first - cd.first) / radii2.first, 
                (-p1d.second - cd.second) / radii2.second)

            var theta = calcAngle(Pair(1.0, 0.0), pd1u)
            var thetaDelta = calcAngle(pd1u, pd2u)
            if (sweepFlag) {
                if (thetaDelta < 0) {
                    thetaDelta += 2 * PI 
                }
            } else {
                if (thetaDelta > 0) {
                    thetaDelta -= 2 * PI
                }
            }
            val clockwise = thetaDelta >= 0 
            
            var theta2 = 0.0
            theta2 = theta + thetaDelta
            val phaiRotMat = Matrix2(cosPhai, -sinPhai, sinPhai, cosPhai)
            var p12Hal = Pair(
                (point1.first + point2.first) / 2,
                (point1.second + point2.second) / 2)
            val c =  phaiRotMat * cd + p12Hal   
             
            val result = EllipseParam(c.first, c.second,
                radii2.first, radii2.second,
                xAxisRotation,
                theta, theta2,
                !clockwise)
            return result 
        }
        /**
         * calc angle 
         * display coordinate angle is clockwise plus
         * cartesian cooridinate angle is counter clockwise plus
         */
        fun calcAngle(vec1 : Pair<Double, Double>,
            vec2: Pair<Double, Double>): Double {
            val inprdct = vec1.first * vec2.first + vec1.second * vec2.second
            val v1Norm = sqrt(vec1.first.pow(2) + vec1.second.pow(2))
            val v2Norm = sqrt(vec2.first.pow(2) + vec2.second.pow(2))
            var acosSign = vec1.first * vec2.second - vec1.second * vec2.first
            // acosSign may be 0. In this case, accosSign need to be 1
            // acosSign = sign(acosSign)
            if (acosSign >= 0) {
                acosSign = 1.0
            } else {
                acosSign = -1.0
            }
            var acosValue =  inprdct / (v1Norm * v2Norm)
            acosValue = max(min(acosValue, 1.0), -1.0)
            val result = acosSign * acos(acosValue)

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
    data class Element(val type: ElementType, val data: List<Double>) 

    /**
     * path 2d ellipse parameter
     */ 
    data class EllipseParam(val x: Double,
        val y: Double,
        val radiusX: Double,
        val radiusY: Double,
        val rotation: Double,
        val startAngle: Double,
        val endAngle: Double,
        val anticlockwise: Boolean)
    
}
