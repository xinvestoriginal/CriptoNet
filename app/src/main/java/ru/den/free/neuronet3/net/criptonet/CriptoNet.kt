package ru.den.free.neuronet3.net.criptonet


import kotlin.math.abs

open class CriptoNet(private val length : Int) {

    class Neuron {

        var name       = ""
        private var weights    : Array<Double>? = null
        private var trainCount : Int = 0

        fun clear(name : String, length : Int)
        {
            this.name = name
            trainCount = 0
            weights = Array(length){ 0.0 }
        }

        fun detect(data : Array<Double>) : Double{
            var res = 0.0
            for (pos in weights!!.indices) {
                res += 1 - abs(weights!![pos] - data[pos])
            }
            return res / weights!!.size
        }

        fun train(data : Array<Double>) : Int
        {
            trainCount++
            for (pos in weights!!.indices) {
                weights!![pos] = weights!![pos] + 2 * (data[pos] - 0.5f) / trainCount
                if (weights!![pos] > 1) weights!![pos] = 1.0
                if (weights!![pos] < 0) weights!![pos] = 0.0
            }
            return trainCount
        }

    }

    private var neurons  = ArrayList<Neuron>() // массив нейронов

    protected fun detect(data : Array<Double>) : String?
    {
        var res : String? = null
        var max = 0.0
        for (n in neurons)
        {
            val d = n.detect(data)
            if (d > max)
            {
                max = d
                res = n.name
            }
        }
        return res
    }

    private fun findByName(name : String) : Neuron?{
        for (n in neurons) if (n.name == name) return n
        return null
    }

    protected fun train(trainingName : String, data : Array<Double>) : String
    {
        var neuron = findByName(trainingName)
        if (neuron == null)
        {
            neuron = Neuron()
            neuron.clear(trainingName, length)
            neurons.add(neuron)
        }
        val trainCount = neuron.train(data)
        return "Имя образа - " + neuron.name +
                 " вариантов образа в памяти - " + trainCount.toString()
    }
}