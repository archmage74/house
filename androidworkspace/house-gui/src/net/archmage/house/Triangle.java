package net.archmage.house;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

class Triangle {

	private final String vertexShaderCode =
			// This matrix member variable provides a hook to manipulate
			// the coordinates of the objects that use this vertex shader
			"uniform mat4 uMVPMatrix;" +
			"uniform mat4 uRotationMatrix;" +

			"attribute vec4 vPosition;" +
			"attribute vec4 aColor;" +
			"attribute vec2 aTexCoordinate;" + // Per-vertex texture coordinate information we will pass in."
			
			"varying vec4 vColor;" +
			"varying vec2 vTexCoordinate;" +

			"void main() {" +
			"    vColor = aColor;" +
			"    vTexCoordinate = aTexCoordinate;" + 
			// the matrix must be included as a modifier of gl_Position
			"    gl_Position =  uMVPMatrix * uRotationMatrix * vPosition;" +
			"}";

	private final String fragmentShaderCode =
			"precision mediump float;" +
			
			"uniform sampler2D uTexture;" + 

			"varying vec4 vColor;" +
			"varying vec2 vTexCoordinate;" +
			
			"void main() {" +
			"    gl_FragColor = texture2D(uTexture, vTexCoordinate);" + // = vColor;
			"}";

	private FloatBuffer vertexBuffer;
	private ShortBuffer drawListBuffer;

	static final int COORDS_PER_VERTEX = 3;
	static final int COLOR_PER_VERTEX = 4;
	static float houseCoords[] = {
			-1.0f,  1.0f, 0.0f, // b nw 0
			 1.0f, 0.0f, 0.0f, 1.0f,
			-1.0f, -1.0f, 0.0f, // b sw 1
			 0.0f, 0.0f, 1.0f, 1.0f,
			 1.0f, -1.0f, 0.0f, // b se 2
			 0.0f, 1.0f, 0.0f, 1.0f,
			 1.0f,  1.0f, 0.0f, // b ne 3
			 1.0f, 0.0f, 0.0f, 1.0f,
			-1.0f,  1.0f, 1.0f, // t nw 4
			 1.0f, 0.0f, 0.0f, 1.0f,
			-1.0f, -1.0f, 1.0f, // t sw 5
			 0.0f, 0.0f, 1.0f, 1.0f,
			 1.0f, -1.0f, 1.0f, // t se 6
			 0.0f, 1.0f, 0.0f, 1.0f,
			 1.0f,  1.0f, 1.0f, // t ne 7
			 1.0f, 0.0f, 0.0f, 1.0f,
	};

    private short drawOrder[] = { 
    		0, 2, 1, 0, 3, 2, // bottom
    		4, 6, 5, 4, 6, 7, // top
    		0, 1, 5, 0, 5, 4, // west
    		1, 2, 6, 1, 6, 5, // south
    		2, 3, 7, 2, 7, 6, // east
    		3, 0, 4, 3, 4, 7, // north
    };

    final static int TEX_COORD_PER_VERTEX = 2;
    final float[] textureCoordinateData = {
	        0.0f, 1.0f, // b nw 0
	        0.0f, 0.0f, // b sw 1
	        1.0f, 0.0f, // b se 2
	        1.0f, 1.0f, // b ne 3
	        0.2f, 0.8f, // t nw 4
	        0.2f, 0.2f, // t sw 5
	        0.8f, 0.2f, // t se 6
	        0.8f, 0.8f, // t ne 7
    };
    
	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

	private int mProgram;

	private int mPositionHandle;

	private int mColorHandle;

	private int mMVPMatrixHandle;
	private int mRotationMatrixHandle;
	
	private FloatBuffer mTextureCoordinates;
	private int mTextureCoordinateHandle;
	private int mTextureUniformHandle;
	private int mTextureDataHandle;


	public static int loadTexture(final Context context, final int resourceId) {
		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
		// Set filtering
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		// Load the bitmap into the bound texture.
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		
		bitmap.recycle();

		return textureHandle[0];
	}

	public Triangle(Context context) {

		vertexBuffer = ByteBuffer.allocateDirect(houseCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(houseCoords).position(0);

		drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        drawListBuffer.put(drawOrder).position(0);
		
		mTextureCoordinates = ByteBuffer.allocateDirect(textureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTextureCoordinates.put(textureCoordinateData).position(0);

		int vertexShader = MyGL20Renderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		Log.i("Triangle", "vertexShader=" + vertexShader);
		int fragmentShader = MyGL20Renderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		Log.i("Triangle", "fragmentShader=" + fragmentShader);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram); // creates OpenGL ES program executables
		
		mTextureDataHandle = Triangle.loadTexture(context, R.drawable.ic_launcher);
	}

	public void draw(float[] mvpMatrix, float[] rotationMatrix) {
		MyGL20Renderer.checkGlError("start draw triangle");

		// Add program to OpenGL ES environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// get handle to vertex shader's vColor member
		mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
		// Enable a handle to the triangle colors
		GLES20.glEnableVertexAttribArray(mColorHandle);

		int stride = (COORDS_PER_VERTEX + COLOR_PER_VERTEX) * 4;

		// set the buffer to read the first coordinate
		vertexBuffer.position(0);
		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, stride, vertexBuffer);

		// set the buffer to read the first color-vector
		vertexBuffer.position(COORDS_PER_VERTEX);
		// Prepare the triangle color data
		GLES20.glVertexAttribPointer(mColorHandle, COLOR_PER_VERTEX, GLES20.GL_FLOAT, false, stride, vertexBuffer);
		
		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		MyGL20Renderer.checkGlError("glGetUniformLocation");
		// MyGL20Renderer.checkGlError("glGetUniformLocation");
		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		MyGL20Renderer.checkGlError("glUniformMatrix4fv");

		// get handle to shape's transformation matrix
		mRotationMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uRotationMatrix");
		MyGL20Renderer.checkGlError("glGetUniformLocation");
		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mRotationMatrixHandle, 1, false, rotationMatrix, 0);
		MyGL20Renderer.checkGlError("glUniformMatrix4fv");

		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
		MyGL20Renderer.checkGlError("glGetUniformLocation");
	    // Set the active texture unit to texture unit 0.
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    // Bind the texture to this unit.
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	    GLES20.glUniform1i(mTextureUniformHandle, 0);
		MyGL20Renderer.checkGlError("glUniform1i");

	    mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoordinate");
		MyGL20Renderer.checkGlError("glGetAttribLocation");
		mTextureCoordinates.position(0);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEX_COORD_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mTextureCoordinates);
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

	    // Draw the object
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
