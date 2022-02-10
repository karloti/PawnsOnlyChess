fun main() {
    val a = readLine()!!.toInt()
    val b = readLine()!!.toInt()
    val c = readLine()!!.toInt()

//    println((a > 0 && b <= 0 && c <= 0) || (a <= 0 && b > 0 && c <= 0) || (a <= 0 && b <= 0 && c > 0))
    println((a > 0) xor (b > 0) xor (c > 0) && !(a > 0 && b > 0 && c > 0))
}

/***
 *  true   false  false  -> OK (result1)
 *  false   true    false  -> OK
 *  false   false   true   -> OK
 *
a AND b     a OR b    a XOR b
a=true, b=true       true       true      false
a=true, b=false      false      true      true
a=false, b=true      false      true      true
a=false, b=false     false      false     false

(a>0 XOR b>0 XOR c>0)


 ***/