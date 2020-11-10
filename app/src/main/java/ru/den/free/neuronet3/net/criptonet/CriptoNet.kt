package ru.den.free.neuronet3.net.criptonet

import kotlin.math.abs

open class CriptoNet(private val length : Int,
                     private val limit  : Int = DEFAULT_VARIANT_COUNT) {

    //result: 76.19   train time: 19251 detect time: 0.4315
    //result: 76.19   train time: 18715 detect time: 0.4394
    //result: 76.19   train time: 20597 detect time: 0.4407
    //result: 76.19   train time: 21508 detect time: 0.4491
    //result: 76.19   train time: 17682 detect time: 0.3585
    //result: 76.19   train time: 17637 detect time: 0.3537
    //result: 93.55   train time: 158961 detect time: 24.3283
    //result: 93.38   train time: 104773 detect time: 14.5116
    //result: 93.38   train time: 111930 detect time: 14.6227

    class CriptoNeuron(var name : String, length : Int) {

        private var weights  = Array(length){ 0.0 }

        private var trainCount : Int = 0

        fun detect(data : Array<Double>) : Double{
            var res = 0.0
            for (pos in weights.indices) {
                res += abs(weights[pos] - data[pos])
            }
            return res
        }

        fun train(data : Array<Double>) : Int
        {
            trainCount++
            for (i in weights.indices) {
                weights[i] = weights[i] + 2 * (data[i] - 0.5f) / trainCount
                if (weights[i] > 1) weights[i] = 1.0
                if (weights[i] < 0) weights[i] = 0.0
            }
            return trainCount
        }

    }

    companion object{

        private const val DEFAULT_VARIANT_COUNT = 500

        private fun maxCorrect(list : ArrayList<CriptoNeuron>, data : Array<Double>) : CriptoNeuron {
            assert(list.isNotEmpty())
            var res = list[0]
            var min = -1.0
            for (n in list){
                val d = n.detect(data)
                if (d < min || min < 0)
                {
                    min = d
                    res = n
                }
            }
            return res
        }

        private fun toList(map : HashMap<String,ArrayList<CriptoNeuron>>) : ArrayList<CriptoNeuron> {
            val res = ArrayList<CriptoNeuron>()
            for (v in map.values) res.addAll(v)
            return res
        }
    }

    private var nMap  = HashMap<String,ArrayList<CriptoNeuron>>()

    fun detect(input : Array<Double>) : String? = maxCorrect(toList(nMap),input).name

    fun train(trainingName : String, data : Array<Double>) : String
    {
        if (!nMap.containsKey(trainingName)){
            nMap[trainingName] = ArrayList()
        }
        val trainCount = if (nMap[trainingName]!!.size < limit)
        {
            val neuron = CriptoNeuron(trainingName, length)
            nMap[trainingName]!!.add(neuron)
            neuron.train(data)
        }else{
            maxCorrect(nMap[trainingName]!!,data).train(data)
        }
        return "Имя образа - $trainingName вариантов образа в памяти - $trainCount"
    }
}