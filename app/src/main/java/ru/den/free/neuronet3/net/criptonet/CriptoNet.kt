package ru.den.free.neuronet3.net.criptonet


import kotlin.math.abs

open class CriptoNet(private val width : Int, private val height : Int) {

    class Neuron {

        var name       = ""
        var weights    : Array<Array<Double>>? = null
        var trainCount : Int = 0

        fun clear(name : String, x : Int, y : Int)
        {
            this.name = name
            trainCount = 0
            weights = Array(y){ Array(x) {0.0} }
        }

        fun detect(data : Array<Array<Double>>) : Double{
            var res = 0.0
            for (yPos in weights!!.indices) {
                for (xPos in weights!![yPos].indices) {
                    res += 1 - abs(weights!![yPos][xPos] - data[yPos][xPos])
                }
            }
            return res / (weights!!.size * weights!![0].size)
        }

        fun train(data : Array<Array<Double>>) : Int
        {
            trainCount++
            for (yPos in weights!!.indices) {
                for (xPos in weights!![yPos].indices) {
                    val v = if (data[yPos][xPos] == 0.0) 0 else 1
                    val arr = weights!![yPos]
                    arr[xPos] = arr[xPos] + 2 * (v - 0.5f) / trainCount
                    if (weights!![yPos][xPos] > 1) weights!![yPos][xPos] = 1.0
                    if (weights!![yPos][xPos] < 0) weights!![yPos][xPos] = 0.0
                }
            }
            return trainCount
        }

    }

    private var neurons  = ArrayList<Neuron>() // массив нейронов

    protected fun detect(data : Array<Array<Double>>) : String?
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

    protected fun train(trainingName : String, data : Array<Array<Double>>) : String
    {
        var neuron = findByName(trainingName)
        if (neuron == null)
        {
            neuron = Neuron()
            neuron.clear(trainingName, width, height);
            neurons!!.add(neuron)
        }
        val trainCount = neuron!!.train(data) // обучим нейрон новому образу
        return "Имя образа - " + neuron.name +
                 " вариантов образа в памяти - " + trainCount.toString()
    }
}