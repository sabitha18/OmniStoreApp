package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityCustomerScanBinding
import com.armada.storeapp.databinding.LayoutConfirmBarcodeBinding
import com.armada.storeapp.ui.utils.mlkit_barcode_scan.BarcodeAnalyzer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_confirm_barcode.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class CustomerScanActivity : AppCompatActivity() {

    private var scannedCode: String = ""
    private var barcodeDialog: Dialog? = null
    lateinit var binding: ActivityCustomerScanBinding

    private var processingBarcode = AtomicBoolean(false)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis

    // Select back camera
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions Not Granted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.launch(Manifest.permission.CAMERA)

        //set title and message
//        binding.lblTitle.text = intent.extras?.getString("title")
//        binding.lblSubTitle.text = intent.extras?.getString("msg")

        //Back Button
        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }

        //Flash Button
        binding.btnFlash.setOnClickListener {
            flashOn = !flashOn
            //Change icon
            val id = if (flashOn) R.drawable.ic_flash_off else R.drawable.ic_flash_on
            binding.btnFlash.setImageDrawable(ContextCompat.getDrawable(this, id))

            try {
                // Bind use cases to lifecycleOwner
                val cam =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

                if (cam.cameraInfo.hasFlashUnit()) {
                    cam.cameraControl.enableTorch(flashOn)
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        processingBarcode.set(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        try {
            // Create an instance of the ProcessCameraProvider,
            // which will be used to bind the use cases to a lifecycle owner.
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

            // Add a listener to the cameraProviderFuture.
            // The first argument is a Runnable, which will be where the magic actually happens.
            // The second argument (way down below) is an Executor that runs on the main thread.
            cameraProviderFuture.addListener({
                // Add a ProcessCameraProvider, which binds the lifecycle of your camera to
                // the LifecycleOwner within the application's life.
                cameraProvider = cameraProviderFuture.get()
                // Initialize the Preview object, get a surface provider from your PreviewView,
                // and set it on the preview instance.
//                preview = Preview.Builder().build().also {
//                    it.setSurfaceProvider(
//                        binding.previewView.surfaceProvider
//                    )
//                }
                preview = Preview.Builder().build()
                binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)
                // Setup the ImageAnalyzer for the ImageAnalysis use case
                val builder = ImageAnalysis.Builder()
//                    .setTargetResolution(Size(1280, 720))


                imageAnalysis = builder.build()
                    .also {
                        it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                            if (processingBarcode.compareAndSet(false, true)) {
                                beep()
                                Log.d("dd--", "Result: $barcode")
                                scannedCode = barcode
                                showScannedBarcode()

                            }
                        })
                    }

                try {
                    // Unbind any bound use cases before rebinding
                    cameraProvider.unbindAll()
                    // Bind use cases to lifecycleOwner
                    val cam =
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

//                    preview.setSurfaceProvider(binding.previewView.surfaceProvider)

                    if (cam.cameraInfo.hasFlashUnit()) {
                        cam.cameraControl.enableTorch(flashOn)
                        binding.btnFlash.visibility = View.VISIBLE
                    }

                } catch (e: Exception) {
                    Log.e("PreviewUseCase", "Binding failed! :(", e)
                }
            }, ContextCompat.getMainExecutor(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun beep() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
    }

    fun showScannedBarcode() {

        try {

            val layoutConfirmBarcodeBinding: LayoutConfirmBarcodeBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(this),
                    R.layout.layout_confirm_barcode,
                    null,
                    false
                )

            layoutConfirmBarcodeBinding?.tvBarcode?.setText("Scanned barcode - $scannedCode")


            layoutConfirmBarcodeBinding.btnConfirm.setOnClickListener() {
                val intent = Intent()
                intent.putExtra("BarcodeResult", scannedCode)
                setResult(RESULT_OK, intent)
                finish()
                barcodeDialog?.dismiss()
            }
            layoutConfirmBarcodeBinding.btnRetry.setOnClickListener() {
                processingBarcode.set(false)
                barcodeDialog?.dismiss()
            }
            if (barcodeDialog == null) {
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(layoutConfirmBarcodeBinding.root)
                barcodeDialog = builder.show()
                barcodeDialog?.setCancelable(false)
            } else if (barcodeDialog != null && barcodeDialog?.isShowing == false) {
                barcodeDialog?.tv_barcode?.text = "Scanned barcode - $scannedCode"
                barcodeDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}