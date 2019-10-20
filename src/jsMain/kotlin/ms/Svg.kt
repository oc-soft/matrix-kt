package ms
import kotlin.collections.Map
import kotlin.collections.HashMap
import kotlin.reflect.*
import kotlin.math.*
import net.ocsoft.Path
import org.w3c.dom.Path2D

/**
 * extension pair object
 */
operator fun Pair<Int, Int>.plus(
    a: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first + a.first, second + a.second)
}

/**
 * extension pair object
 */
operator fun Pair<Int, Int>.minus(
    a: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first - a.first, second - a.second)
}

/**
 * extension pair object
 */
operator fun Pair<Double, Double>.plus(
    a: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(first + a.first, second + a.second)
}

/**
 * extension pair object
 */
operator fun Pair<Double, Double>.minus(
    a: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(first - a.first, second - a.second)
}

      

/**
 * svg extension
 */
class Svg {
    
    /**
     * class instance
     */
    companion object {
        /**
         * element type, parsing data mapping
         */
        var HANDLER_MAP_VALUE: Map<Path.ElementType, 
            (Path.Element, PathParsing)->Boolean>? = null

        /**
         * element type parsing data mapping
         */
        val HANDLER_MAP: Map<Path.ElementType, 
            (Path.Element, pathParsing: PathParsing)->Boolean>
            get() {
                var result: Map<Path.ElementType, 
                    (Path.Element,
                        pathParsing: PathParsing)->Boolean>? = null
                if (HANDLER_MAP_VALUE == null) {
                    val hdr_map = HashMap<Path.ElementType,
                        (Path.Element, PathParsing)->Boolean>()

                    hdr_map[Path.ElementType.M] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleMoveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.m] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleMoveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.Z] = {
                       elm: Path.Element, pp: PathParsing ->
                       handleClosepath(elm, pp)
                    }
                    hdr_map[Path.ElementType.L] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleLineto(elm, pp)
                    }
                    hdr_map[Path.ElementType.l] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleLineto(elm, pp)
                    }
                    hdr_map[Path.ElementType.H] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleHorizontalLineto(elm, pp)
                    }
                    hdr_map[Path.ElementType.h] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleHorizontalLineto(elm, pp)
                    }
                    hdr_map[Path.ElementType.V] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleVerticalLineto(elm, pp)
                    }
                    hdr_map[Path.ElementType.v] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleVerticalLineto(elm, pp)
                    }
                    hdr_map[Path.ElementType.C] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.c] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.S] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleSmoothCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.s] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleSmoothCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.Q] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleQuadraticCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.q] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleQuadraticCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.T] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleQuadraticSmoothCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.t] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleQuadraticSmoothCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.A] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleEllipticalArcCurveto(elm, pp)
                    }
                    hdr_map[Path.ElementType.a] = {
                        elm: Path.Element, pp: PathParsing ->
                        handleEllipticalArcCurveto(elm, pp)
                    }
                    HANDLER_MAP_VALUE = hdr_map
                }
                result = HANDLER_MAP_VALUE!!
                return result
            }
        /**
         * create Path2D from string
         */
        fun createPath2D(pathData: String): Path2D {
            val pathParsing = PathParsing(Path2D())
            Path.parse(pathData, {
                var result = false
                val hdlr = HANDLER_MAP[it.type]
                if (hdlr != null) {
                    result = hdlr(it, pathParsing)
                }
                result
            }, {
                print(it)
            }) 
            val result = pathParsing.path
            return result
        } 



        /**
         * handle moveto command
         */
        fun handleMoveto(elm: Path.Element,
            pp: PathParsing): Boolean {
           
            var pos = Pair(elm.data[0], elm.data[1])
            if (Path.ElementType.m == elm.type) {
                pos = relativeToAbsolute(pp.cp, pp.cb, pos)
            }
            pp.cp = pos
            pp.lm = pos
            pp.path.moveTo(pos.first.toDouble(), pos.second.toDouble())
            var result = true
            for (i in 2..elm.data.size - 1 step 2) {
                val lCommand = Path.Element(Path.ElementType.l, 
                    elm.data.slice(IntRange(i, i + 1)))
                result = handleLineto(lCommand, pp)
                if (!result) {
                    break
                }
            }
            return result
        }
        /**
         * handle closepath
         */
        fun handleClosepath(elm: Path.Element,
            pp: PathParsing): Boolean {
            pp.lm = pp.cp
            pp.path.closePath()  
            return true
        }

        /**
         * handle lineto command
         */
        fun handleLineto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var coordConversion = ::noConversion
            if (elm.type == Path.ElementType.l) {
                coordConversion = ::relativeToAbsolute
            }
            for (i in 0..elm.data.size - 1 step 2) {
                var pos = coordConversion(
                    pp.cp, pp.cb,
                    Pair(elm.data[i], elm.data[i + 1]))
                pp.cp = pos
                pp.path.lineTo(pos.first.toDouble(), pos.second.toDouble())
            }
            return true
        }

        /**
         * equality conversion
         */
        fun noConversion(cp: Pair<Double, Double>, cb: Double,
            pos: Pair<Double, Double>): Pair<Double, Double> {
            return pos    
        } 

        /**
         * handle lineto command
         */
        fun handleHorizontalLineto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var lineType = Path.ElementType.L
            var yCoord = pp.cp.second
            if (elm.type == Path.ElementType.h) {
                lineType = Path.ElementType.l
                yCoord = 0.0
            }
            val coordinates = ArrayList<Double>()
            for (i in 0..elm.data.size - 1) {
                coordinates.add(elm.data[i])
                coordinates.add(yCoord)
            }
            val result = handleLineto(
                Path.Element(lineType, coordinates), pp)
            return result
        }

        /**
         * handle lineto command
         */
        fun handleVerticalLineto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var lineType = Path.ElementType.L
            var xCoord =  pp.cp.first
            if (elm.type == Path.ElementType.v) {
                lineType = Path.ElementType.l
                xCoord = 0.0
            }
            val coordinates = ArrayList<Double>()
            for (i in 0..elm.data.size - 1) {
                coordinates.add(xCoord)
                coordinates.add(elm.data[i])
            }
            val result = handleLineto(
                Path.Element(lineType, coordinates), pp)
            return result
        }

        /**
         * handle curve to
         */
        fun handleCurveto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var coordConversion = ::noConversion
            if (elm.type == Path.ElementType.c) {
                coordConversion = ::relativeToAbsolute
            }
            for (i in 0..elm.data.size - 1 step 6) {
                var pos = Array<Pair<Double, Double>>(3) {
                    coordConversion(
                        pp.cp, pp.cb,
                        Pair(elm.data[i + it * 2], 
                            elm.data[i + it * 2 + 1]))
                }
                pp.lcp = pos[1]
                pp.cp = pos[2]
                pp.path.bezierCurveTo(
                    pos[0].first.toDouble(),
                    pos[0].second.toDouble(),
                    pos[1].first.toDouble(),
                    pos[1].second.toDouble(),
                    pos[2].first.toDouble(),
                    pos[2].second.toDouble())
            }
            val result = true  
            return result
        }

        /**
         * handle smooth curve to
         */
        fun handleSmoothCurveto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var coordConversion = ::noConversion
            if (elm.type == Path.ElementType.s) {
                coordConversion = ::relativeToAbsolute
            }
            val convertedCoords = ArrayList<Double>()
            var lp = pp.cp
            var lcp = pp.lcp 
            val cb = pp.cb
            for (i in 0..elm.data.size - 1 step 4) {
                var pos = Array<Pair<Double, Double>>(2) {
                    coordConversion(
                        lp, cb,
                        Pair(elm.data[i + it * 2], 
                            elm.data[i + it * 2 + 1]))
                }
                
                val cp1 = (lp - lcp) + lp
                val cp2 = pos[0] 
                val p = pos[1]
                convertedCoords.addAll(cp1.toList())
                convertedCoords.addAll(cp2.toList())
                convertedCoords.addAll(p.toList())
                lcp = pos[0]
                lp = pos[1]
            }
            val result = handleCurveto(
                Path.Element(Path.ElementType.C, convertedCoords), pp)
              
            return result
        }

        /**
         * handle quadratic curve to
         */
        fun handleQuadraticCurveto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var coordConversion = ::noConversion
            var result = false
            if (elm.type == Path.ElementType.q) {
                coordConversion = ::relativeToAbsolute
            }
            for (i in 0..elm.data.size - 1 step 4) {
                var pos = Array<Pair<Double, Double>>(2) {
                    coordConversion(
                        pp.cp, pp.cb,
                        Pair(elm.data[i + it * 2], 
                            elm.data[i + it * 2 + 1]))
                }
                pp.lcp = pos[0]
                pp.cp = pos[1]
                pp.path.quadraticCurveTo(
                    pos[0].first.toDouble(), pos[0].second.toDouble(),
                    pos[1].first.toDouble(), pos[1].second.toDouble())
                result = true
            }
            return result 
        }

        /**
         * handle quadratic smooth curve to
         */
        fun handleQuadraticSmoothCurveto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var coordConversion = ::noConversion
            if (elm.type == Path.ElementType.t) {
                coordConversion = ::relativeToAbsolute
            }
            val convertedCoords = ArrayList<Double>()
            var lp = pp.cp
            var lcp = pp.lcp 
            for (i in 0..elm.data.size - 1 step 2) {
                var pos = coordConversion(
                        pp.cp, pp.cb, 
                        Pair(elm.data[i * 2], 
                            elm.data[i * 2 + 1]))
                
                val cp = (lp - lcp) + lp
                convertedCoords.addAll(cp.toList())
                convertedCoords.addAll(pos.toList())
                lcp = cp
                lp = pos
            }
            val result = handleQuadraticCurveto(
                Path.Element(Path.ElementType.Q, convertedCoords), pp)
              
            return result
        }

        /**
         * handle elliptical arc 
         */
        fun handleEllipticalArcCurveto(elm: Path.Element,
            pp: PathParsing): Boolean {
            var coordConversion = ::noConversion
            if (elm.type == Path.ElementType.a) {
                coordConversion = ::relativeToAbsoluteF
            }
            val convertedCoords = ArrayList<Double>()
           var result = false

            if (elm.type == Path.ElementType.A
                || elm.type == Path.ElementType.a) { 
               for (idx in 0..elm.data.size - 1 step 7) {
                    var lp = pp.cp
                    val p1f = lp  
                    val p2f = coordConversion(lp, pp.cb,
                        Pair(elm.data[idx + 5], elm.data[idx + 6]))

                    val ellipseParam = Path.resolveArcParam(
                        Pair(elm.data[idx + 0], 
                            elm.data[idx + 1]),
                        (elm.data[idx + 2].toDouble() * PI) / 180,
                        elm.data[idx + 3].toInt(), 
                        elm.data[4].toInt(), p1f, p2f)
                    if (ellipseParam != null) {

                        pp.path.ellipse(
                            ellipseParam.x.toDouble(), 
                            ellipseParam.y.toDouble(),
                            ellipseParam.radiusX.toDouble(), 
                            ellipseParam.radiusY.toDouble(),
                            ellipseParam.rotation.toDouble(),
                            ellipseParam.startAngle.toDouble(),
                            ellipseParam.endAngle.toDouble(),
                            ellipseParam.anticlockwise)
                        pp.cp = p2f
                        result = true
                    } else {
                        var lineElem: Path.Element? = null
                        val coords = elm.data.slice(5..6)
                        if (elm.type == Path.ElementType.A) {
                            lineElem = Path.Element(Path.ElementType.L,
                                coords)   
                        } else {
                            lineElem = Path.Element(Path.ElementType.l,
                                coords)   
                        }
                        result = handleLineto(lineElem, pp)
                    }
                    if (!result) {
                        break
                    }
                }
            }
            return result
        }

        /**
         * relative to absolute position
         */
        fun relativeToAbsolute(cp: Pair<Double, Double>, cb: Double,
            rlpos: Pair<Double, Double>): Pair<Double, Double> {

            val posFlt = relativeToAbsoluteF(cp, cb, rlpos)
            return posFlt 
         }

        /**
         * relative to absolute position
         */
        fun relativeToAbsoluteF(cp: Pair<Double, Double>, cb: Double,
            rlpos: Pair<Double, Double>): Pair<Double, Double> {
            val rad = (cb.toDouble() / 180.0) * PI
            val rot = doubleArrayOf(
                rlpos.first * cos(rad) + rlpos.second * sin(rad),
                rlpos.first * sin(rad) + rlpos.second * cos(rad)
            )
            return Pair(cp.first + rot[0], cp.second + rot[1])
 
        } 

    }

    /**
     * use this instance during parsing svg path data.
     */
    class PathParsing(path: Path2D) {
        /**
         * path data
         */
        val path: Path2D = path
        /**
         * current point
         */
        var cp = Pair(0.0, 0.0)
        /**
         * current bearing
         */
        var cb = 0.0

        /**
         * last moveto
         */
        var lm = Pair(0.0, 0.0)

        /**
         * last control point
         */
        var lcp = Pair(0.0, 0.0)
    }

}

