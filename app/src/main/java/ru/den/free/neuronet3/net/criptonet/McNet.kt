package ru.den.free.neuronet3.net.criptonet

class McNet(width: Int, height: Int) : CriptoNet(width, height) {

    fun detect(arr : Array<Array<Int>>) : String?{
        val data = Array(arr.size){ i ->
            Array(arr[i].size){ j -> if (arr[i][j] > 0) 1.0 else 0.0 /*arr[i][j] / 255.0*/}
        }
        return super.detect(data)
    }

    fun train(trainingName : String,  arr : Array<Array<Int>>) : String{
        val data = Array(arr.size){ i ->
            Array(arr[i].size){ j -> if (arr[i][j] > 0) 1.0 else 0.0 /*arr[i][j] / 255.0*/}
        }
        return train(trainingName,data)
    }
}