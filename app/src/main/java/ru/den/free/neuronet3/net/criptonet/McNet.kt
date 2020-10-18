package ru.den.free.neuronet3.net.criptonet

class McNet(width: Int, height: Int) : CriptoNet(width * height) {

    private fun convertArray(arr : Array<Array<Int>>) : Array<Double>{
        val data = Array(arr.size * arr[0].size){ 0.0 }
        for (y in arr.indices){
            for (x in arr[0].indices){
                val v = arr[y][x]
                val pos = x + arr[0].size * y
                data[pos] = if (v > 0) 1.0 else 0.0
            }
        }
        return data
    }

    fun detect(arr : Array<Array<Int>>) = super.detect(convertArray(arr))

    fun train(trainingName : String,
              arr : Array<Array<Int>>) = super.train(trainingName,convertArray(arr))

}