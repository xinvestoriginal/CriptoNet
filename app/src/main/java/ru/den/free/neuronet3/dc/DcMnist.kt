package ru.den.free.neuronet3.dc

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.den.free.neuronet3.mnist.MnistDataReader
import ru.den.free.neuronet3.mnist.MnistMatrix
import ru.den.free.neuronet3.net.criptonet.CriptoNet
import java.io.IOException

class DcMnist {

    interface IMnistLoader{
        fun onMnist(net : CriptoNet)
    }

    interface IMnistDetect{
        fun onDetectResult(str : String?)
    }

    companion object{

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

        fun asyncMnistDetect(net : CriptoNet, img: Array<Array<Int>>, listener : IMnistDetect){
            GlobalScope.launch(Dispatchers.IO) {
                val res = net.detect(toCriptoInput(img))
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
                //if (list!!.size < 500)
                    list!!.add(m.data)
            }
            return res
        }

        @Throws(IOException::class)
        fun exec() : CriptoNet {
            var mnistMatrixList = MnistDataReader().readData("data/train-images.idx3-ubyte","data/train-labels.idx1-ubyte")
            printMnistMatrix(mnistMatrixList[mnistMatrixList.size - 1]!!)
            val map = toMap(mnistMatrixList)
            val net = CriptoNet(28 * 28)
            val trainStartTime = System.currentTimeMillis()
            for (l in map.keys){
                val list = map[l]!!
                for (data in list){
                    net.train(l, toCriptoInput(data))
                }
            }
            val trainTime = System.currentTimeMillis() - trainStartTime
            mnistMatrixList = MnistDataReader().readData("data/t10k-images.idx3-ubyte", "data/t10k-labels.idx1-ubyte")
            var rightCount = 0
            val checkStartTime = System.currentTimeMillis()
            for (m in mnistMatrixList){
                val l = net.detect(toCriptoInput(m!!.data))
                if (l != null && l == m.label.toString()) rightCount++
            }
            val detectTime = (System.currentTimeMillis() - checkStartTime) / mnistMatrixList.size.toFloat()
            printMnistMatrix(mnistMatrixList[0]!!)
            Log.e("!!!", "result: " +
                    (100 * (rightCount.toDouble() / mnistMatrixList.size)).toString() +
                    "   train time: " + trainTime + " detect time: " + detectTime)
            return net
        }

        private fun printMnistMatrix(matrix: MnistMatrix) {
            Log.e("!!!", "label: " + matrix.label)
            for (r in 0 until matrix.numberOfRows) {
                var str = ""
                for (c in 0 until matrix.numberOfColumns) {
                    //Log.e("!!!",matrix.getValue(r, c) + " ");
                    var subStr = matrix.getValue(r, c).toString() + ""
                    if (subStr.length == 1) {
                        subStr = "  $subStr"
                    } else if (subStr.length == 2) {
                        subStr = " $subStr"
                    }
                    str += "$subStr "
                }
                Log.e("!!!", str)
            }
        }
    }

}