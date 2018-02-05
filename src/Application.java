import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import gui.ControlFrame;
import renderer.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {
	private static final int FPS = 60; // animator's target frames per second

	public void start() {
		try {
			
			
			Frame testFrame = new Frame("TestFrame");
			testFrame.setSize(512, 384);

			// setup OpenGL version
			GLProfile profile = GLProfile.getMaximum(true);
			GLCapabilities capabilities = new GLCapabilities(profile);

			// The canvas is the widget that's drawn in the JFrame
			GLCanvas canvas = new GLCanvas(capabilities);
			Renderer ren = new Renderer();

			Frame controlFrame = new ControlFrame(ren);

			canvas.addGLEventListener(ren);
			canvas.addMouseListener(ren);
			canvas.addMouseMotionListener(ren);
			canvas.addKeyListener(ren);
			canvas.setSize(512, 384);

			testFrame.add(canvas);

			//shutdown the program on windows close event

			//final Animator animator = new Animator(canvas);
			final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

			testFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					new Thread() {
						@Override
						public void run() {
							if (animator.isStarted())
								animator.stop();
							System.exit(0);
						}
					}.start();
				}
			});
			testFrame.setTitle(ren.getClass().getName());
			testFrame.pack();
			testFrame.setVisible(true);
			animator.start(); // start the animation loop

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Application().start());
	}

}