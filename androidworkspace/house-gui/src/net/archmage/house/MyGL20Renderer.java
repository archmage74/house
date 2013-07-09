package net.archmage.house;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class MyGL20Renderer implements GLSurfaceView.Renderer {

	private Triangle mTriangle;

	private float[] mProjMatrix = new float[16];
	private float[] mVMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private int muMVPMatrixHandle;
	private float[] mRotationMatrixX = new float[16];
	private float[] mRotationMatrixY = new float[16];
	
	public float mAngleX = 0;
	public float mAngleY = 0;
	
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

		// initialize a triangle
		mTriangle = new Triangle();
		
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Combine the projection and camera view matrices
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        // Apply the combined projection and camera view transformations
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

//        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrixX , 0, mAngleX, -1.0f, 0, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrixX, 0, mMVPMatrix, 0);
        Matrix.setRotateM(mRotationMatrixY , 0, mAngleY, 0, -1.0f, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrixY, 0, mMVPMatrix, 0);
        
        mTriangle.draw(mMVPMatrix);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
	    GLES20.glViewport(0, 0, width, height);

	    float ratio = (float) width / height;

	    // this projection matrix is applied to object coordinates
	    // in the onDrawFrame() method
	    Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}

}

