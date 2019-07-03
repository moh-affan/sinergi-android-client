package dev.affan.sinergi

fun BooleanArray.mapInPlace(transform: (index: Int, Boolean) -> Boolean) {
    var idx = 0
    this.forEachIndexed { index, _ -> this[index] = transform(idx++, this[index]) }
}