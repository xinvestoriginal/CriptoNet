package ru.den.free.neuronet3.mnist

class MnistMatrix(val numberOfRows: Int, val numberOfColumns: Int) {

    val data: Array<Array<Int>> = Array(numberOfRows) { Array(numberOfColumns) {0} }

    var label = 0
    fun getValue(r: Int, c: Int): Int {
        return data[r][c]
    }

    fun setValue(row: Int, col: Int, value: Int) {
        data[row][col] = value
    }

}