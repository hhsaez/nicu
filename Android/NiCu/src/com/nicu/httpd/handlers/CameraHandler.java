package com.nicu.httpd.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.utils.Log;

public class CameraHandler extends RequestHandler {

	private Camera camera;
	private InputStream inputStream;

	private PictureCallback pictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			Bitmap input = BitmapFactory.decodeByteArray(data, 0, data.length);
			Bitmap output = Bitmap.createScaledBitmap(input, 640, 480, false);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			output.compress(CompressFormat.JPEG, 20, bos);
			setInputStream(new ByteArrayInputStream(bos.toByteArray()));
		}
	};

	public CameraHandler() {
		super("/robot/camera");
	}

	@Override
	public void onServerStart() {
		super.onServerStart();
		findCamera();
		
		if (this.camera != null) {
			this.camera.startPreview();
		}
	}

	@Override
	public void onServerStop() {
		super.onServerStop();
		setInputStream(null);
		releaseCamera();
	}

	/**
	 * TODO: There is a flaw with this design. As it is, we're always a
	 * frame behind, since the 'takePicture' request is asynchronous. 
	 * So far it's not a problem
	 */
	public Response handleRequest(IHTTPSession session) {
		if (this.camera == null) {
			return new Response(Response.Status.INTERNAL_ERROR,
					RequestHandler.RESPONSE_MIME_PLAINTEXT,
					"Cannot access camera");
		}

		this.camera.takePicture(null, null, pictureCallback);
		
		if (this.getInputStream() == null) {
			return new Response(Response.Status.INTERNAL_ERROR,
					RequestHandler.RESPONSE_MIME_PLAINTEXT,
					"Invalid picture result");
		}
		
		return new Response(Response.Status.OK, RequestHandler.RESPONSE_MIME_JPG, getInputStream());
	}

	private void findCamera() {
		int cameraId = -1;
		int cameraCount = Camera.getNumberOfCameras();
		for (int i = 0; i < cameraCount; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				break;
			}
		}

		if (cameraId >= 0) {
			this.camera = Camera.open(cameraId);
		} else {
			Log.error("Cannot find camera");
		}
	}

	private void releaseCamera() {
		if (this.camera != null) {
			this.camera.stopPreview();
			this.camera.release();
			this.camera = null;
		}
	}
	
	private InputStream getInputStream()
	{
		return this.inputStream;
	}
	
	private void setInputStream(InputStream is)
	{
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.inputStream = is;
	}

}
