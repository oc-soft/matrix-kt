package net.ocsoft

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
        fun parse(pathData: String, handler: ((Element) -> Boolean)) {

            pathData.forEach {
                if (curType == null) {
                    when (it) {
                        'M' -> curType = MoveTo
                        'm' -> curType = moveTo
                        'L' -> curType = LineTo
                        'l' -> curType = lineTo
                    }
                } else {
                    
                }    
            }    
        }       

        /**
         *  parse start
         */
        fun parse0(parser: Parser, 
            handler: ((Element) -> Boolean)): Boolean {
            var result = parseWspZeroOrMor(parser)
            parseMoveto(parser, handler) 
            (parseMoveto(parser, hander) && parseDrawto(parser, handler))
            return result
        }

        /**
         * parse draw to
         */
        fun parseDrawto(parser: Parser,
            handler: ((Element) -> Boolean)): Boolean {
            var parseMethods : Array<(Parser, ((Elem)->Boolean)) -> Boolean>
            = arrayOf(
                parseMoveto,
                parseLineto,
                parseHorizontalLineto,
                parseVerticalLineto,
                parseCurveto,
                parseSmoothCurveto,
                parseQuadraticBezierCurveto,
                parseSmoothQuadraticBezierCurveto,
                parseEllipticalArc,
                parseBearing,
                parseCatmullRom
            ) 
            var result = false
            parseMethods.find { 
                result = it(parsr, handler)
                result
            }
            return result
        }

        /**
         * parse moveTo
         */
        fun parseMoveto(parser: Parser, 
            handler: ((Element) -> Boolean)): Boolean {
            var result = false
            val pc = parser.peekChar  
            if ('M' == pc || 'm' == pc) {
                
                parser.nextChar
                parseWspZeroOrMore  
                val coordinates = ArrayList<Pair<Int, Int>>()
                result = paseCoodinatePairSequence(parser,
                    coordinates)
                if (result) {
                    var elemType = ElementType.valueOf(pc.toString())  
                    val flatCoordinates = ArrayList<Int>()
                    coordinates.forEach { 
                        flatCoordinates.addAll(it.toArray()) 
                    } 
                    result = handler(Element(elementType, flatCoordinates))
                    if (result) {
                        result = paseClosepath(parser, handler)
                    }
                }
            }
            return result
        }

        /**
         * parse close path
         */
        fun parseClosepath(parser: Parser,
            handler: ((Element)->Boolean)): Boolean {
            var result = false
            val pc = parser.peekChar 
            if ('Z' == pc || 'z' == pc) {
                result = handler(
                    ElementType.valueOf(pc.toString(),
                    ArrayList<Int>()) 
                parser.nextChar
            }
            return result
        }
        
        /**
         * parse
         */
        fun parseLineto(parser: Parser,
            handler: ((Element)->Boolean)): Boolean {
            var result = false
            val pc = parser.peekChar
            if ('L' == pc || 'l' == pc) {
                parser.nextChar
                parseWspZeroOrMore(parser)
                val coordinates = ArrayList<Pair<Int, Int>>()
                result = parseCoordinatePairSequenc(parser, coordinates)
                val elem = Element(ElementType.valueOf(pc),
                        flatCoordinates)
                if (result) {
                    val flatCoordinates = ArrayList<Float>()
                    coordinates.forEach {
                        flatCoordinates.add(it.first)
                        flatCoordinates.add(it.second)
                    } 
                    result = handler(elem)
                } else {
                    val closepathRes: Element? = null
                    result =  parseClosepath(parser, { 
                        closepathRes = it 
                    })
                    if (result) {
                        result = handler(elem)
                    }
                    if (result) {
                        result = handler(closepathRes)
                    }
                }
            }
            return result
        }

        /**
         * parse horizontal lineto
         */
        fun parseHorizontalLineto(parser: Parser,
            handler: ((Element)->Boolean)): Boolean {
            var result = false
            val pc = parser.peekChar
            if ('H' == pc || 'h' == pc) {
                parser.nextChar
                parseWspZeroOrMore(parser)
                val coordinates = ArrayList<Float>()
                result = parseCoordinateSquence(parser, coordinates)
                if (result) {
                    result = handler(Element(ElementType.valueOf(pc),
                        coordinates))
                }
            }
            return result
        }

        /**
         * parse vertical lineto
         */
        fun parseVerticalLineto(parser: Parser,
            handler: ((Element)->Unit)): Boolean {
            var result = false
            val pc = parser.peekChar
            if ('H' == pc || 'h' == pc) {
                parser.nextChar
                parseWspZeroOrMore(parser)
                val coordinates = ArrayList<Int>()
                result = parseCoordinateSquence(parser,coordinates)
                if (result) {
                    result = handler(Element(ElementType.valueOf(pc),
                        coordinates))
                }
            }
            return result
        }

        /**
         * parse curveto
         */
        fun parseCurveto(parser: Parser,
            handler: ((Element)->Unit)): Boolean {
            var result = false
            val pc = parser.peekChar
            if ('C'== pc || 'c' == pc) {
                parser.nextChar
                parseWspZeroOrMore(parser)
                val coordinates = ArrayList<Int>()
                result = parseCurvetoCoordinateSquence(parser,
                    coordinates)
                val elemType = ElementType.valueOf(pc.toString())
                if (result) {
                    result = handler(Element(elemType, coordinate))
                } else {
                    val coordinatePairs = ArrayList<Pair<Int, Int>>()
                    parseCoordiantePairSequence(parser, coordinatePairs)
                    coordinatePairs.forEach {
                        coordinates.add(it.first)
                        coordinates.add(it.second)
                    }
                    val closepathRes: Element? = null
                    result = parseClosepath({ closepathRes = it })
                    if (result) {
                        result = handler(Element(elemType, coordinates))
                    }
                    if (result) {
                        result = handler(closepathRes)
                    }
                }
            }
            return result
        } 
        
        /**
         * parse curveto coorditate sequence
         */
        fun parseCurvetoCoordinateSequence(parser: Parser,
            coordinates: List<Pair<Int, Int>>?): Boolean {
            var result = false
             
            result = parseCoordiatePairTriplet(parser, coordinats)
            if (result) {
                parseCommaWsp(parser)
                result = parseCurvetoCoordinateSequence(parser, coordinates)
            }
            return result
        }
        /**
         * parse smooth curveto
         */
        fun parseSmoothCurveto(parser: Parser,
            handler: ((Element)->Unit)): Boolean {
            var result = false
            val pc = parser.peekChar
            if ('S'== pc || 's' == pc) {
                parser.nextChar
                parseWspZeroOrMore(parser)
                val coordinatePairs = ArrayList<Pair<Int, Int>>()
                result = parseSmoothCurvetoCoordinateSequence(
                    parser, coordinatePairs)
                if (result) {
                    val coordinates = ArrayList<Int>()
                    coordinatePairs.forEach {
                        coordinates.add(it.first),
                        coordinates.add(it.second)
                    }
                    result = handler(
                        Element(ElementType.valueOf(pc.toString()), 
                        coordinates))
                }
            } 
            return result
        }
        /**
         * parse smooth curveto coordinate sequence
         */
        fun parseSmoothCurvetoCoordinateSequence(
            parser: Parser,
            coordinates: MutableList<Pair<Int, Int>>?): Boolean {
            val result = false
            result = parsePairDouble(parser, coordinates) 
            if (result) {
                parseCommaWsp(parser)
                parseSmoothCurvetoCoordinateSequence(parser, coordinates)
            } 
            return result
        }

        /**
         * parse quadratic bezier curve
         */
        fun parseQuadraticBezierCurveto(parser: Paraser,
            handler: ((Element)->Boolean)): Boolean {
            var result = false
            val pc = parser.peekChar
            if ('Q' == pc || 'q' == pc') {
                parser.nextChar
                val coordinatePairs = ArrayList<Pair<Int, Int>>()
                parseCommaWsp(parser)
                result = parseQuadraticBezierCurvetoCoordinateSequence(parser, 
                    coordiratePairs)
                
                if (result) {
                    val coordinates = ArrayList<Int>()
                    coordinatePairs.forEach {
                        coordinates.add(it.first)
                        coordinates.add(it.second)
                    }
                    result = handler(
                        Element(ElementType.valueOf(pc.toString()),
                        coordinates))
                }
            } 
            return result
        } 

        /**
         * parse quadratic curveto coordinate sequence
         */
        fun parseQuadraticCurvetoCoordinateSequence(parser: Parser,
            coordinates: MutableList<Pair<Int, Int>>?): Boolean {
            var result = false
            result = parseCoordinatePairDouble(parser, coordinates)
            if (result) {
                parseWsp(parser)
                parseQuadraticBezierCurvetoSequence(parser,
                    coordinates)
            } 
            return result
        }

        /**
         * smooth quadratic bezier curveto
         */
        fun parseSmoothQuadraticBezierCurveto(
            parser: Paraser,
            handler: ((Element)->Boolean):) Boolean {
            var result = false
            var pc = parser.peekNext
            if ('T' == pc || 't' == pc) {
                parser.nextChar
                parseWspZeroOrMore(parser)
                val coordinatePairs = Array<Pair<Int, Int>>()
                result = parseCoordiatePairSequence(parser,
                    coordinatePairs)
                if (result) {
                    val coordinates = Array<Int>()
                    coordinatePairs.forEach {
                        coordinates.add(it.first)
                        coordinates.add(it.second)
                    } 
                    result = handler(
                        Element(ElementType.valueOf(pc.toString(),
                            coordinates))) 
                } 
            }
            return result
        }

        /**
         * elliptical arc
         */
        fun parseEllipticalArc(parser: Parser,
            handler: ((Element)->Boolean)): Boolean {
            var result = false
            var pc = parser.peekChar
            if ('A' == pc || 'a' == pc) {
                parser.nextChar
                val args = ArrayList<Int>()
                result = parseEllipticalArcArgumentSequence(parser, args) 
                if (result) {
                    result = handler(Element(
                        ElementType.valueOf(pc.toString()),
                        args))
                }
            }
            return result
        }

        /**
         * parse elliptical arc argument sequence
         */
        fun parseElliptticalArcArgumentSequence(parser: Parser,
            ellipticalArgs: MutablList<Int>?): Boolean {
            var result = parseEllipticalArcArgument(parser,
                ellipticalArgs)
            if (result) {
                parseCommaWsp(parser)
                parseEllipticalArcArgumentSequence(parser,
                    elliptialArgs)
            }
            return result
        }
        /**
         * parse elliptical arc argument
         */
        fun parseEllipticalArgArgument(parser: Parser,
            ellipticalArcArgument: MutableList<Int>?): Boolean {
            var nums : IntArray(7)
            var result = paserNumber(parser, { nums[0] = it })
            if (result) {
                parseCommaWsp(parser)
                result = parseNumber(parser, { nums[1] = it })
            }
            if (result) {
                parseCommaWsp(parser)
                result = parseNumber(parser, { nums[2] = it })
            }
            if (result) {
                result = parseCommaWsp(parser)
            }
            if (result) {
                result = parseFlag(parser, { nums[3] = it })
            } 
            if (result) {
                parseCommaWsp(parser)
                result = parseFlag(parser, { nums[4] = it })
            }
            if (result) {
                parseCommaWsp(parser)
                result = paserCoordinatePair(parser {
                    num[5] = it.first
                    num[6] = it.second
                })
            }
            if (result) {
                nums.forEach {
                    ellipticalArcArgument?.add(it)
                }
            }
            
            return result
        }
        
        /**
         * parse elliptical arc argument
         */ 
        fun parseEllipticalArcArgument(parser: Paraser) {
            var result = paserNumber(parser)
            if (result) {
                parseCommaWsp(parser)
                result = parseNumber(parser)
            }
            if (result) {
                parseCommaWsp(parser)
                result = parseNumber(parser)
            }
            if (result) {
                result = parseCommaWsp(parser)
            }
            if (result) {
                result = parseFlag(parser)
            }
            if (result) {
                parseCommaWsp(parser)
                result = parseFlat(parser)
            }
            if (result) {
                parseCommaWsp(parser)
                result = paserClosepath(parser)
            }
            return result
        }


        /**
         * parse catmull-rom 
         */
        fun parseCatmullRom(parser: Parser): Boolean {
            var pc = parser.peekChar
            if ('R' == pc || 'r' == pc) {
                parseWspZeroOrMore(parser)
                result =  parseCatmullRomArgumentSequence(parser) 
            }
            return result
        }

        /**
         * parse catmull-rom  arugment sequence
         */
        fun parseCatmullRomArgumentSequence(parser: Parser): Boolean {
            var result = parseCoodinatePair(parser)
            if (result) {
                result = parseCoodinatePair(parser)
            } 
            if (result) {
                result = paserCoordinatePair(parser)
            }
            if (result) {
                while (parseCoodinatePair(parser)) {
                } 
            }
            return result
        }
        /**
         * parse cooditate pair double
         */
        fun parseCoodiatePairDouble(parser: Parser) {
            result = parseCoordinatePair(parser)
            if (result) {
                parseCommaWsp(parser)
                result = parseCoodiatePair(parser)
            }
            return result
        }
        
        /**
         * parse cooditate pair triplet
         */
        fun parseCoodiatePairTriplet(parser: Parser,
            coordinatePairs: MutableList<Pair<Int, Int>>?): Boolean {
            val pairList = ArrayList<Pair<Int, Int>>() 
            result = parseCoordinatePair(parser, { pairList.add(it) })
            if (result) {
                for (i in 0..1) {
                    parseCommaWsp(parser)
                    result = parseCoodiatePair(parser, { pairList.add(it) })
                    if (!result) {
                        break;
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
            parser: Parser,
            coodinates: MutablList<Pair<Int, Int>>?): Boolean {
            var result = parseCoordinatePair(parser, { coordinates?.add(it) })
            if (result) {
                parseCommaWsp(parser)
                parseCoordinatePairSequence(parser, coodinates)
            }
            return result
        }

        /**
         * parse coordinate sequence
         */
        fun parseCoordinateSequence(parser: Parser,
            coordinates: MutableList<Int>?): Boolean {
            var result = parseCoordinate(parser, { coordinates?.add(it) })
            if (result) {
                parseCommaWsp(parser)
                parseCoordinateSequence(parser, coordinates)
            }
            return result
        }


        /**
         * parse coordinate pair
         */
        fun parseCoordinatePair(parser: Parser,
            handler : (Pair<Int, Int>->Unit)?): Boolean {
            val numbers = intArrayOf(0, 0)
            var result = parseCoordinate(parser, { numbers[0] = it })
            if (result) {
                parseCommaWsp(parser)
                result = parseCoordinate(parser, { numbers[1] = it })
                if (result) {
                    if (handler != null) {
                        handler(Pair<Int>(numbers[0], numbers[1]))
                    }
                }
            }
            return result
        }

        /**
         * parse coordinate
         */
        fun parseCoordinate(parser: Parser, 
            hanlder: ((value: Int)->Unit)?): Boolean {
            var result = false
            var sign = 1
            parseSign(parser, { sign = it })
            var number: Int = 0 
            result = parseNumber(parser, { number = it })
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
        fun parseSign(parser: Parser,
            handler: ((Int)->Unit)?): Boolean {
            var result = false
            var pc = parser.peekChar
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
                parser.nextChar
            } 
            return result
        }

        /**
         * parse number
         */
        fun parseNumber(parser: Parser,
            handler: ((Int)->Unit)?): Boolean {
            var result = false 
            var hit = false
            val number = 0 
            do {
                var pc = parser.peekChar
                if (pc != null) {
                    hit = '0' <= pc && pc <= '9'
                } else {
                    hit = false
                }
                if (hit) {
                    result = true
                    number *= 10
                    number += (pc - '0') 
                    parser.nextChar
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
        fun parseFlag(parser: Parser): Boolean {
            var result = false
            var pc = parser.peekChar
            result = '0' == pc || '1' == pc
            if (result) {
                parser.nextChar
            }
            return result
        }

        /**
         * parse comma and white space
         */
        fun parseCommaWsp(parser: Parser): Boolean {
            var result = false
            result = parseWsp(parser)
            if (result) {
                var pc = parser.peekChar
                if (',' == pc) {
                    parser.nextChar
                }
                while (parseWsp(parser)) {
                }
            } else {
                var pc = parser.peekChar
                if (',' == pc) {
                    result = true
                    while(parseWsp(parser)) {
                    }
                }
            } 
            return result 
        }

        /**
         * parse white space
         */
        fun parseWsp(parser: Parser) {
            var result = false
            var pc = parser.peekChar 
            if ('\U0009' == pc
                || '\u0020' == pc
                || '\u000A' == pc
                || '\u000C' == pc
                || '\u000D' == pc) {
                result = true
                parser.nextChar
            }
            return result
        }
 
        /**
         * parse white space zero or more
         */
        fun parseWspZeroOrMore(parser: Parser): Boolean {
            var result = parseWsp(parser)
            if (result) {
                result = parseWspZeroOrMore(parser)
            }
            return result
        }

        
    }

    /**
     * path string parser
     */
    class Parser(val pathString: String) {
        
        /**
         * current index
         */
        var index: Int = 0 
       
        /**
         *  token start index
         */
        var lexTokenStart: Int = -1
        /**
         * token end index
         */
        var lexTokenEnd: Int = -1

        /**
         * last lex token
         */
        var lastLexToken: LexToken?
        /**
         * next token
         */
        val nextLexToken: LexToken?
            get() {
                var result: LexToken? = null
                var tokenStart: Int = -1
                var tokenEnd: Int = -1
                if (peekChar != nul) {
                    if (peekChar in wsp) {
                        result = Was
                        tokenStart = index
                        nextChar
                        tokneEnd = index
                    }
                    if (result != null) {
                        while (peekChar in wsp) {
                            nextChar 
                            tokenEnd = index 
                        }  
                        if (peekChar == ',') {
                            nextChar
                            tokenEnd = index
                            while (peekChar in wsp) {
                                nextChar
                                tokenEnd = index
                            } 
                            result = CommaWsp
                        }
                    } else if (peekChar == ',') {
                        nextChar
                        tokenEnd = index
                        while (peekChar in wsp) {
                            nextChar
                            tokenEnd = index
                        }
                        result = CommaWsp
                    }
                    if (result == null) {
                        if (peekChar == '0' || peekChar == '1') {
                            tokenStart = index
                            result = Flag 
                            nextChar
                            tokenEnd = index
                        } 
                        if (result != null) {
                            if (peekChar in numberSet) {
                                result = Number
                                while (peekChar in numberSet) {
                                    nextChar
                                    tokenEnd = index
                                }  
                            }
                        }
                    }
                    if (result == null) {
                        if (peekChar == '+' || peekChar == '-') {
                            result = Sign
                            tokenStart = index
                            nextChar
                            tokenEnd = index
                        }
                    }
                }
                if (result != null) {
                    this.lexTokenStart = tokenStart
                    this.lexTokenEnd = tokenEnd
                    this.lastLexToken = result
                }
                
                return result
            } 
        /**
         * read next token from path string
         */
        val nextToken: Token?
            get() {
                var result: Token? = null
                
                return result
            }
        /**
         * peek character
         */
        val peekChar: Character?
            get() {
                var result: Character? = null
                if (index < pathString.length) {
                    result = pathString[index] 
                }
                return result
            }
        /**
         * next character
         */
        val nextChar: Character?
            get() {
                var result = peekChar
                if (result != null) {
                    index++
                }
                return result
            }
        /**
         * white space set
         */
        var wspValue: Set<Character>? = null
        /**
         * white space set
         */
        val wsp: Set<Character>
            get() {
                if (wspValue == null) {
                    val wspSet = HashSet<Character>()
                    wspSet.add('\u0009')
                    wspSet.add('\u0020')
                    wspSet.add('\u000A')
                    wspSet.add('\u000D')                    
                    WspValue = wspSet
                }
                return wspValue!!
            }
        /**
         * number set
         */
        var numberSetValue: Set<Character>? = null
        /**
         * number set
         */
        val numberSet: Set<Character>
            get() {
                if (numberSetValue == nul) {
                    numberSetValue = HashSet<Character>()
                    for (i in 0..9) {
                        numberSetValue.add('0' + i)
                    }
                }   
                return numberSetValue!!
            }
    }

    /**
     * token
     */
    enum class Token {
        Moveto,
        Close,
        Lineto,
        HorizontalLineto,
        VerticalLineto,
        Curveto,
        CurvetoCoordinateSequence,
        SmoothCurveto,
        SmoothCurvetoCoordinateSequence,
        QuadraticBezierCurveto,
        QuadraticBezierCurvetoSequence,
        SmoothQuadraticBezierCurveto,
        EllipticalArc,
        EllipticalArcArgument,
        EllipticalArcClosingArgument,
        Bearing,
        BearingArgumentSequence,
        CatmullRom,
        CatmullRomArgumentSequence,
        CoordiantePairDouble,
        CoordinatePairTriplet,
        CoordinatePairSequence,
        CoordinatePair,
        Coordinate
    }
    /**
     *
     */
    enum class LexToken {
        Sign,
        Number,
        Flag,
        CommaWsp,
        Wsp
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
        c
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
    data class Element(type: ElementType, data: FloatArray) 
    
}
