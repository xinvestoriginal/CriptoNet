package ru.den.free.neuronet3.net.criptonet


open class CriptoNet(private val length : Int) {

    //result: 76.19   train time: 19251 detect time: 0.4315
    //result: 76.19   train time: 18715 detect time: 0.4394
    //result: 76.19   train time: 20597 detect time: 0.4407
    //result: 76.19   train time: 21508 detect time: 0.4491
    //result: 76.19   train time: 17682 detect time: 0.3585

    companion object{
        fun maxCorrect(list : ArrayList<CriptoNeuron>, data : Array<Double>) : CriptoNeuron {
            assert(list.isNotEmpty())
            var res = list[0]
            var max = 0.0
            for (n in list){
                val d = n.detect(data)
                if (d > max)
                {
                    max = d
                    res = n
                }
            }
            return res
        }

        fun toList(map : HashMap<String,ArrayList<CriptoNeuron>>) : ArrayList<CriptoNeuron> {
            val res = ArrayList<CriptoNeuron>()
            for (v in map.values) res.addAll(v)
            return res
        }
    }

    private var nMap  = HashMap<String,ArrayList<CriptoNeuron>>()

    fun detect(data : Array<Double>) : String? = maxCorrect(toList(nMap),data).name

    fun train(trainingName : String, data : Array<Double>) : String
    {
        if (!nMap.containsKey(trainingName)){
            nMap[trainingName] = ArrayList()
        }
        if (nMap[trainingName]!!.isEmpty())
        {
            val neuron = CriptoNeuron(trainingName, length)
            nMap[trainingName]!!.add(neuron)
        }
        val neuron = maxCorrect(nMap[trainingName]!!,data)
        val trainCount = neuron.train(data)
        return "Имя образа - " + neuron.name +
                 " вариантов образа в памяти - " + trainCount.toString()
    }
}