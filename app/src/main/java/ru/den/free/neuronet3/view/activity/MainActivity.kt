package ru.den.free.neuronet3.view.activity

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import ru.den.free.neuronet3.R
import ru.den.free.neuronet3.dc.DcMnist
import ru.den.free.neuronet3.net.criptonet.CriptoNet
import ru.den.free.neuronet3.view.custom.FingerLine


class MainActivity : AppCompatActivity(), DcMnist.IMnistLoader, FingerLine.IFingerLine,
    DcMnist.IMnistDetect {

    private var net : CriptoNet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pbMain.visibility = View.VISIBLE
        //flMain.visibility = View.GONE
        flMain.setListener(this)
        DcMnist.asyncMnistNetLoad(this)
        Toast.makeText(this,"wait 1 - 5 min",Toast.LENGTH_LONG).show()
        
    }

    override fun onMnist(net: CriptoNet) {
        pbMain.visibility = View.GONE
        flMain.visibility = View.VISIBLE
        this.net = net
    }

    override fun onBitmap(bmp : Bitmap, img: Array<Array<Int>>) {
        ivMainPreview.setImageBitmap(bmp)
        if (net != null) {
            DcMnist.asyncMnistDetect(net!!,img,this)
        }else{
            onDetectResult("Net is null.")
        }
    }

    override fun onDetectResult(str: String?) {
        if (str != null) etMainDetectResult.setText(str)
    }
}