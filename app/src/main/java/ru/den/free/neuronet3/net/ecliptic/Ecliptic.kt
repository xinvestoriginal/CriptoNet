package ru.den.free.neuronet3.net.ecliptic

import kotlin.math.abs

open class Ecliptic(private val length : Int) {

    class EItem(var name : String, length : Int) {

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

        private fun maxCorrect(list : ArrayList<EItem>, data : Array<Double>) : EItem {
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

        //private fun toList(map : HashMap<String,ArrayList<EItem>>) : ArrayList<EItem> {
        //    val res = ArrayList<EItem>()
        //    for (v in map.values) res.addAll(v)
        //    return res
        //}
    }

    private var nMap  = HashMap<String,EItem>()
    private var iList = ArrayList<Int>()

    init {
        for (i in 0 until length) iList.add(i)
    }

    private fun adaptInput(data : Array<Double>) : Array<Double>{
        val res = ArrayList<Double>()
        for (i in iList) res.add(data[i])
        return res.toTypedArray()
    }

    fun detect(input : Array<Double>) : String? = maxCorrect(ArrayList(nMap.values),adaptInput(input)).name

    fun train(trainingName : String, input : Array<Double>) : String
    {
        val data = adaptInput(input)
        if (!nMap.containsKey(trainingName)){
            nMap[trainingName] = EItem(trainingName, length)
        }
        val trainCount = nMap[trainingName]!!.train(data)
        return "Имя образа - $trainingName вариантов образа в памяти - $trainCount"
    }
}