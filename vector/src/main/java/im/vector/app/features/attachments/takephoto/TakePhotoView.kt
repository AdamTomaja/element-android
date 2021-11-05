/*
 * Copyright (c) 2021 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.attachments.takephoto

import android.Manifest
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.util.Pair
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import im.vector.app.R
import im.vector.app.core.extensions.getMeasurements
import im.vector.app.databinding.ViewTakePhotoBinding
import im.vector.app.features.home.room.detail.RoomDetailFragment
import im.vector.app.features.home.room.detail.RoomDetailFragment_Factory
import java.lang.Exception
import java.util.concurrent.ExecutorService
import kotlin.math.max

private const val ANIMATION_DURATION = 250

class TakePhotoView(activity: Activity, fragment: Fragment, context: Context, inflater: LayoutInflater, var callback:Callback?): PopupWindow(context) {
    interface Callback {

    }

    private val activity: Activity
    private val fragment: Fragment
    private val context: Context
    private val views: ViewTakePhotoBinding;
    private var anchor: View? = null

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    init {
        this.activity = activity;
        this.context = context
        this.fragment = fragment;
        contentView = inflater.inflate(R.layout.view_take_photo, null, false)
        views = ViewTakePhotoBinding.bind(contentView);
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        animationStyle = 0
        @Suppress("DEPRECATION")
        setBackgroundDrawable(BitmapDrawable())
        inputMethodMode = INPUT_METHOD_NOT_NEEDED
        isFocusable = true
        isTouchable = true
    }

    fun show(anchor: View, isKeyboardOpen: Boolean) {
        this.anchor = anchor
        val anchorCoordinates = IntArray(2)
        anchor.getLocationOnScreen(anchorCoordinates)
        if (isKeyboardOpen) {
            showAtLocation(anchor, Gravity.NO_GRAVITY, 0, anchorCoordinates[1] + anchor.height)
        } else {
            val contentViewHeight = if (contentView.height == 0) {
                contentView.getMeasurements().second
            } else {
                contentView.height
            }
            showAtLocation(anchor, Gravity.NO_GRAVITY, 0, anchorCoordinates[1] - contentViewHeight)
        }
        contentView.doOnNextLayout {
            animateWindowInCircular(anchor, contentView)
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        startCamera()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also { it.setSurfaceProvider(views.viewFinder.createSurfaceProvider()) }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(fragment, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("takePhoto", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun animateWindowInCircular(anchor: View, contentView: View) {
        val coordinates = getClickCoordinates(anchor, contentView)
        val animator = ViewAnimationUtils.createCircularReveal(contentView,
                coordinates.first,
                coordinates.second,
                0f,
                max(contentView.width, contentView.height).toFloat())
        animator.duration = ANIMATION_DURATION.toLong()
        animator.start()
    }

    private fun getClickCoordinates(anchor: View, contentView: View): Pair<Int, Int> {
        val anchorCoordinates = IntArray(2)
        anchor.getLocationOnScreen(anchorCoordinates)
        val contentCoordinates = IntArray(2)
        contentView.getLocationOnScreen(contentCoordinates)
        val x = anchorCoordinates[0] - contentCoordinates[0] + anchor.width / 2
        val y = anchorCoordinates[1] - contentCoordinates[1]
        return Pair(x, y)
    }
}
