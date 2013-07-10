package net.archmage.house;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class MyGL20Renderer implements GLSurfaceView.Renderer {

	private Triangle mTriangle;

	private float[] mProjMatrix = new float[16];
	private float[] mVMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private int muMVPMatrixHandle;
	private float[] mRotationMatrixZ = new float[16];

	public float mAngleX = 0;
	public float mAngleZ = 0;

	private Context context;

	public MyGL20Renderer(Context context) {
		this.context = context;
	}

	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		GLES20.glClearColor(0.0f, 0.5f, 0.5f, 1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);

		// initialize a triangle
		mTriangle = new Triangle(context);

		Matrix.setLookAtM(mVMatrix, 0,
				5.0f, 5.0f, 2.0f, // eye
				0.0f, 0.0f, 0.3f, // center
				-0.2f, -0.2f, 0.5f); // up
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClearDepthf(1.0f);
		MyGL20Renderer.checkGlError("onDrawFrame 1");
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		MyGL20Renderer.checkGlError("onDrawFrame 2");

		// Combine the projection and camera view matrices
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		MyGL20Renderer.checkGlError("onDrawFrame 3");
		// GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		MyGL20Renderer.checkGlError("onDrawFrame 4");
		Matrix.setRotateM(mRotationMatrixZ, 0, mAngleZ, 0, 0, -1.0f);

		mTriangle.draw(mMVPMatrix, mRotationMatrixZ);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float zoom = 2.0f;
		float ratio = (float) width / height / zoom;

		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1 / zoom, 1 / zoom, 3, 10);
	}

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("MyGLRenderer", glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

}
