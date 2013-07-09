package net.archmage.house;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyGLSurfaceView extends GLSurfaceView {

	private static final float TOUCH_SCALE_FACTOR = 0.2f;
	private float mPreviousX;
	private float mPreviousY;
	private MyGL20Renderer mRenderer;
	
	private int actionDownX = -1;
	private int actionDownY = -1;
	
	private View blendControlPanel;

	public MyGLSurfaceView(Context context) {
		super(context);
		init();
	}

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		setEGLContextClientVersion(2);
		mRenderer = new MyGL20Renderer();
		setRenderer(mRenderer);
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public void setBlendControlPanel(View blendControlPanel) {
		this.blendControlPanel = blendControlPanel;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			actionDownX = (int) e.getX();
			actionDownY = (int) e.getY();
			break;
		case MotionEvent.ACTION_UP:
			if (actionDownX == (int) e.getX() && actionDownY == (int) e.getY()) {
				blendControlPanel.setVisibility(VISIBLE);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("ACTION_MOVE");
			float dx = x - mPreviousX;
			float dy = y - mPreviousY;

			// reverse direction of rotation above the mid-line
			if (y > getHeight() / 2) {
				dx = dx * -1;
			}

			// reverse direction of rotation to left of the mid-line
			if (x < getWidth() / 2) {
				dy = dy * -1;
			}

			mRenderer.mAngleX += dy * TOUCH_SCALE_FACTOR;
			mRenderer.mAngleY += dx * TOUCH_SCALE_FACTOR;

			requestRender();
			break;
		}

		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

}
