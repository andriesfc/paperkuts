package paperkuts.random

import kotlin.random.Random

fun Int.random(): Int = Random.nextInt(0, this)
fun Int.random(upto:Int):Int = Random.nextInt(this, upto)