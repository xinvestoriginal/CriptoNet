package ru.den.free.neuronet3.mnist

import android.util.Log
import ru.den.free.neuronet3.app.app
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException

class MnistDataReader {

    @Throws(IOException::class)
    fun readData(dataFilePath: String?, labelFilePath: String?): Array<MnistMatrix?> {
        val dataInputStream = DataInputStream(
            BufferedInputStream(
                app().assets.open(
                    dataFilePath!!
                )
            )
        )
        //DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFilePath)));
        val magicNumber = dataInputStream.readInt()
        val numberOfItems = dataInputStream.readInt()
        val nRows = dataInputStream.readInt()
        val nCols = dataInputStream.readInt()
        Log.e("!!!", "magic number is $magicNumber")
        Log.e("!!!", "number of items is $numberOfItems")
        Log.e("!!!", "number of rows is: $nRows")
        Log.e("!!!", "number of cols is: $nCols")
        val labelInputStream = DataInputStream(
            BufferedInputStream(
                app().assets.open(
                    labelFilePath!!
                )
            )
        )
        //DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(labelFilePath)));
        val labelMagicNumber = labelInputStream.readInt()
        val numberOfLabels = labelInputStream.readInt()
        Log.e("!!!", "labels magic number is: $labelMagicNumber")
        Log.e("!!!", "number of labels is: $numberOfLabels")
        val data = arrayOfNulls<MnistMatrix>(numberOfItems)
        assert(numberOfItems == numberOfLabels)
        for (i in 0 until numberOfItems) {
            val mnistMatrix = MnistMatrix(nRows, nCols)
            mnistMatrix.label = labelInputStream.readUnsignedByte()
            for (r in 0 until nRows) {
                for (c in 0 until nCols) {
                    mnistMatrix.setValue(r, c, dataInputStream.readUnsignedByte())
                }
            }
            data[i] = mnistMatrix
        }
        dataInputStream.close()
        labelInputStream.close()
        return data
    }
}