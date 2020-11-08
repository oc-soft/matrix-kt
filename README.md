#  Matrix library for kotlin

This is very simple matrix library for kotlin. 

# matrix 2x2 operation

You can operate matrix calculation like followings.

## Calculate inverse matrix

``` kotlin
import net.oc_soft.Matrix2
val mat = Matrix2(2, 5, 4, 6)
// calculate inverse matrix
val matInv = mat.inverse()
// matE is identity matrix
val matE = mat * matInv!!
println(mat1)
println(matInv)
println(matE)
```

The result is here

``` 
m00 : 2.0, m01 : 5.0, m10 : 4.0, m11 : 6.0
m00 : -0.75, m01 : 0.625, m10 : 0.5, m11 : -0.25
m00 : 1.0, m01 : 0.0, m10 : 0.0, m11 : 1.0
```

Some time the result of multiplication matrix and invert matrix is not
complete identity matrix for floating point arithmetic error. But It is
nearly equal identity matrix.

``` kotlin
val mat = Matrix2(2, 5, 4, 7)
val matInv = mat.inverse()
val matE = mat * matInv!!
println(matE)
println(1.0 - matE.m00 < 0.0000001)
println(matE.m01 < 0.0000001)
```

Each component is not equal identity matrix component. But each component value
is almost equal identify matrix component.

```
m00 : 0.9999999999999996, m01 : 2.220446049250313E-16, m10 : -8.881784197001252E-16, m11 : 1.0000000000000004
true
true
```

I am working in progress to write examples.


## Get rotation matrix

I offered the easy way to calculate rotation matrix.

``` kotlin
import net.oc_soft.Matrix2

val mat = MatrrotMat = Matrix2.rotate(kotlin.math.PI / 6.0)

val vec30Deg = rotMat * doubleArrayOf(1.0, 0.0) 

println(rotMat)
println(vec30Deg.joinToString())
println(kotlin.math.sin(kotlin.Math.PI / 6.0) - vec30Deg[1])
```

You would get following output.

```
m00 : 0.8660254037844387, m01 : -0.49999999999999994, m10 : 0.49999999999999994, m11 : 0.8660254037844387
0.8660254037844387, 0.49999999999999994
0.0
m00 : 0.8660254037844387, m01 : -0.49999999999999994, m10 : 0.49999999999999994, m11 : 0.8660254037844387

```
# matrix 3x3 operation

Matrix 3x3 operation is availabled too.

``` kotlin
ipmport knet.ocosf_soft.Matrix3

val mat = Matrix3(-5, 0, -1, 1, 2, -1, -3, 4, 1)
val det = mat.determinanant()

println(mat)
println(det)
```

You would have following result.

```
m00 : -5.0, m01 : 0.0, m02 : -1.0, m10 : 1.0, m11 : 2.0, m12 : -1.0, m20 : -3.0, m21 : 4.0, m22 : 1.0
-40.0
```

You could calculate invert matrix too.

``` kotlin
val matInv = mat.inverse()
println(matInv)
```

```
m00 : -0.15, m01 : 0.1, m02 : -0.05, m10 : -0.05, m11 : 0.2, m12 : 0.15, m20 : -0.25, m21 : -0.5, m22 : 0.25

```

For floating arithmetic error, you might not have equality between matrix
identity and the multiple result about matrix and invert matrix. But it is
almost equals matrix identity.

``` kotlin
val matI = mat * matInv!!

println(matI)
println(matI[2, 0] < 0.000001)
```

```
m00 : 1.0, m01 : 0.0, m02 : 0.0, m10 : 0.0, m11 : 1.0, m12 : 0.0, m20 : -5.551115123125783E-17, m21 : 0.0, m22 : 1.0
true
```
