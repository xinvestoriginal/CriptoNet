package ru.den.free.neuronet3.net.criptonet

import kotlin.math.abs

class CriptoNeuron(var name : String, length : Int) {

    private var weights =  Array(length){ 0.0 }
    private var trainCount : Int = 0

    fun detect(data : Array<Double>) : Double{
        var res = 0.0
        for (pos in weights.indices) {
            res += 1 - abs(weights[pos] - data[pos])
        }
        return res / weights.size
    }

    fun train(data : Array<Double>) : Int
    {
        trainCount++
        for (pos in weights.indices) {
            weights[pos] = weights[pos] + 2 * (data[pos] - 0.5f) / trainCount
            if (weights[pos] > 1) weights[pos] = 1.0
            if (weights[pos] < 0) weights[pos] = 0.0
        }
        return trainCount
    }

}