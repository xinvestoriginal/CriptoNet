package ru.den.free.neuronet3.dc

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.den.free.neuronet3.mnist.MnistDataReader
import ru.den.free.neuronet3.mnist.MnistMatrix
import ru.den.free.neuronet3.net.criptonet.CriptoNet
import ru.den.free.neuronet3.net.ecliptic.Ecliptic
import java.io.IOException

class DcMnist {

    interface IMnistLoader{
        fun onMnist(net : Ecliptic)
    }

    interface IMnistDetect{
        fun onDetectResult(str : String?)
    }

    companion object{

        fun asyncMnistDetect(net : Ecliptic, img: Array<Array<Int>>, listener : IMnistDetect){
            GlobalScope.launch(Dispatchers.IO) {

                val res = net.detect(toCriptoInput(img))
                if (res != null) printMnistMatrix(res!!,img)
                launch(Dispatchers.Main) {
                    listener.onDetectResult(res)
                }
            }
        }

        fun asyncMnistNetLoad(listener : IMnistLoader){
            GlobalScope.launch(Dispatchers.IO) {
                val net = exec()
                launch(Dispatchers.Main) {
                    listener.onMnist(net)
                }
            }
        }

        private fun toMap(source : Array<MnistMatrix?>) : HashMap<String,ArrayList<Array<Array<Int>>>>{
            val res = HashMap<String,ArrayList<Array<Array<Int>>>>()
            for (m in source){
                if (!res.containsKey(m!!.label.toString())){
                    res[m.label.toString()] = ArrayList()
                }
                val list = res[m.label.toString()]
                list!!.add(m.data)
            }
            return res
        }

        private fun toCriptoInput(arr : Array<Array<Int>>) : Array<Double>{
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

        private fun printMnistMatrix(label: String, data: Array<Array<Int>>) {
            Log.e("!!!", "label: $label")
            var str = ""
            for (r in data.indices) {
                if (str.isNotEmpty())
                    str += "\n   "
                for (c in data[0].indices) {
                    var subStr = data[r][c].toString()
                    if (subStr.length == 1) {
                        subStr = "  $subStr"
                    } else if (subStr.length == 2) {
                        subStr = " $subStr"
                    }
                    str += "$subStr "
                }
            }
            Log.e("!!!", str)
        }

        @Throws(IOException::class)
        private fun exec() : Ecliptic {
            var mnistMatrixList = MnistDataReader().readData(
                "data/train-images.idx3-ubyte","data/train-labels.idx1-ubyte")
            var matrix = mnistMatrixList[mnistMatrixList.size - 1]!!
            printMnistMatrix(matrix.label.toString(),matrix.data)
            val map = toMap(mnistMatrixList)
            val net = Ecliptic(28 * 28)
            val trainStartTime = System.currentTimeMillis()
            for (l in map.keys){
                val list = map[l]!!
                for (data in list){
                    net.train(l, toCriptoInput(data))
                }
            }
            val trainTime = System.currentTimeMillis() - trainStartTime
            mnistMatrixList = MnistDataReader().readData(
                "data/t10k-images.idx3-ubyte", "data/t10k-labels.idx1-ubyte")
            var rightCount = 0
            val checkStartTime = System.currentTimeMillis()
            for (m in mnistMatrixList){
                val l = net.detect(toCriptoInput(m!!.data))
                if (l != null && l == m.label.toString()) rightCount++
            }
            val detectTime = (System.currentTimeMillis() - checkStartTime) / mnistMatrixList.size.toFloat()
            matrix = mnistMatrixList[0]!!
            printMnistMatrix(matrix.label.toString(),matrix.data)
            Log.e("!!!", "result: " +
                    (100 * (rightCount.toDouble() / mnistMatrixList.size)).toString() +
                    "   train time: " + trainTime + " detect time: " + detectTime)
            return net
        }
    }

}