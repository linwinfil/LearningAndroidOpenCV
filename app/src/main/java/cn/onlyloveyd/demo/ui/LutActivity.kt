package cn.onlyloveyd.demo.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.onlyloveyd.demo.R
import cn.onlyloveyd.demo.databinding.ActivityLutBinding
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.utils.Converters

/**
 * LUT 查找表
 * author: yidong
 * 2020/2/21
 */
class LutActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityLutBinding
    private lateinit var mRgb: Mat

    private var mR: Int = 0
        set(value) {
            field = value
            doLutSingle(value, mG, mB)
        }
    private var mG: Int = 100
        set(value) {
            field = value
            doLutSingle(mR, value, mB)
        }
    private var mB: Int = 255
        set(value) {
            field = value
            doLutSingle(mR, mG, value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lut)

        mRgb = Mat()
        val bgr = Utils.loadResource(this, R.drawable.lena)
        Imgproc.cvtColor(bgr, mRgb, Imgproc.COLOR_BGR2RGB)
        showMat(mRgb)
        bgr.release()

        mBinding.btLutMulti.setOnClickListener {
            doLutMulti()
        }

        mBinding.btLutSingle.setOnClickListener {
            doLutSingle(mR, mG, mB)
        }

        mBinding.seekBarR.progress = (mR / 255f * 100f).toInt()
        mBinding.seekBarG.progress = (mG / 255f * 100f).toInt()
        mBinding.seekBarB.progress = (mB / 255f * 100f).toInt()
        mBinding.seekBarR.setOnSeekBarChangeListener(seekBarListener)
        mBinding.seekBarG.setOnSeekBarChangeListener(seekBarListener)
        mBinding.seekBarB.setOnSeekBarChangeListener(seekBarListener)
        mBinding.btReset.setOnClickListener {
            mR = 0
            mG = 100
            mB = 255
        }
    }

    private var seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            val value = (progress / 100f * 255f).toInt()
            when (seekBar) {
                this@LutActivity.mBinding.seekBarR -> { mR = value }
                this@LutActivity.mBinding.seekBarG -> { mG = value }
                this@LutActivity.mBinding.seekBarB -> { mB = value }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }

    private fun doLutSingle(r: Int, g: Int, b: Int) {
        val lutOneByteArray = ByteArray(256)
        for (i in 0..255) {
            if (i in 0..100) lutOneByteArray[i] = b.toByte()
            if (i in 101..200) lutOneByteArray[i] = g.toByte()
            if (i > 200) lutOneByteArray[i] = r.toByte()
        }
        val lutTable = Converters.vector_uchar_to_Mat(lutOneByteArray.toList())
        val result = Mat()
        Core.LUT(
            mRgb,       //src，输入图像矩阵，其数据类型只能是CV_8U
            lutTable,   //lut，256个像素灰度值的查找表，单通道或者与src通道数相同
            result      //dst，输出图像矩阵，其尺寸与src相同，数据类型与lut相同
        )
        showMat(result)
    }

    private fun doLutMulti() {
        val lutOneByteArray = ByteArray(256)
        for (i in 0..255) {
            if (i in 0..100) lutOneByteArray[i] = 0
            if (i in 101..200) lutOneByteArray[i] = 100
            if (i > 200) lutOneByteArray[i] = 255.toByte()
        }
        val lutOne = Converters.vector_uchar_to_Mat(lutOneByteArray.toList())

        val lutTwoByteArray = ByteArray(256)
        for (i in 0..255) {
            if (i in 0..100) lutOneByteArray[i] = 0
            if (i in 101..150) lutOneByteArray[i] = 100
            if (i in 151..200) lutOneByteArray[i] = 150.toByte()
            if (i > 200) lutOneByteArray[i] = 255.toByte()
        }

        val lutTwo = Converters.vector_uchar_to_Mat(lutTwoByteArray.toList())

        val lutThreeByteArray = ByteArray(256)
        for (i in 0..255) {
            if (i in 0..100) lutOneByteArray[i] = 100
            if (i in 101..200) lutOneByteArray[i] = 200.toByte()
            if (i > 200) lutOneByteArray[i] = 255.toByte()
        }
        val lutThree = Converters.vector_uchar_to_Mat(lutThreeByteArray.toList())

        val lutTable = Mat()
        Core.merge(listOf(lutOne, lutTwo, lutThree), lutTable)
        lutOne.release()
        lutTwo.release()
        lutThree.release()

        val result = Mat()
        Core.LUT(mRgb, lutTable, result)
        showMat(result)

        lutTable.release()
        result.release()
    }


    private fun showMat(source: Mat) {
        val bitmap = Bitmap.createBitmap(source.width(), source.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(source, bitmap)
        mBinding.ivLena.setImageBitmap(bitmap)
    }

    override fun onDestroy() {
        mRgb.release()
        super.onDestroy()
    }
}