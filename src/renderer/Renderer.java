package renderer;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import oglutils.*;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;
import util.MeshGenerator;

import java.awt.event.*;

/**
 * GLSL sample:<br/>
 * Draw 3D geometry, use camera and projection transformations<br/>
 * Requires JOGL 2.3.0 or newer
 *
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2015-09-05
 */

public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

	private int width, height, ox, oy, polygonMode = GL2GL3.GL_LINE;
	int isReflectorGrid, lightCalcType, attenuationEnabled, textureMap, textureType, transparency,animateNorm, shapeNorm, timeNorm;

	int isReflector, attenuation, lightCalculationType, shapeNrGrid, textureMapGrid, textureTypeGrid, transparencyGrid, normalLengthNorm, animateNormals, transparencyNorm;
	private float normalLength;
	
	private int meshSize;

	OGLBuffers cube, grid;
	OGLTextRenderer textRenderer;
	OGLTexture normTex;
	
	int shaderGrid, locMatGrid, locLightGrid, locEyeGrid, locTimeGrid, shaderNormal, locMatNormals;

	int shape = 5;

	Vec3D lightPos = new Vec3D(4, 2, 5);

	Camera cam = new Camera();
	Mat4 proj;
	float time = 0;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		// relfector is not chosen
		isReflector = 0;
		// environment attenuation is off in default 
		attenuation = 0;
		// per pixel lightning calculation;
		lightCalculationType = 1;
		// normal mapping = 0, paralax = 1
		textureMap = 0;
		// default texture 0 is bricks
		textureType = 3;

		transparency = 0;
		
		normalLength = 0.1f;
		
		animateNormals = 1;
		
		meshSize = 10;

		// check whether shaders are supported
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		OGLUtils.shaderCheck(gl);

		// get and set debug version of GL class
		gl = OGLUtils.getDebugGL(gl);
		glDrawable.setGL(gl);

		OGLUtils.printOGLparameters(gl);

		textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());
		
		shaderNormal = ShaderUtils.loadProgram(gl, "/normals");
		shaderGrid = ShaderUtils.loadProgram(gl, "/gridOrig");

		//*
		createBuffers(gl);

		normTex = new OGLTexture2D(gl, "/textures/bricksn.png");

		locMatGrid = gl.glGetUniformLocation(shaderGrid, "mat");
		locLightGrid = gl.glGetUniformLocation(shaderGrid, "lightPos");
		locEyeGrid = gl.glGetUniformLocation(shaderGrid, "eyePos");
		locTimeGrid = gl.glGetUniformLocation(shaderGrid, "time");
		isReflectorGrid = gl.glGetUniformLocation(shaderGrid, "isReflector");
		attenuationEnabled = gl.glGetUniformLocation(shaderGrid, "attenuationEnabled");
		lightCalcType = gl.glGetUniformLocation(shaderGrid, "lightCalcType");
		shapeNrGrid = gl.glGetUniformLocation(shaderGrid, "shape");
		textureMapGrid = gl.glGetUniformLocation(shaderGrid, "textureMap");
		textureTypeGrid = gl.glGetUniformLocation(shaderGrid, "textureType");
		transparencyGrid = gl.glGetUniformLocation(shaderGrid, "transparency");

		//mostly for geometry shader
		locMatNormals = gl.glGetUniformLocation(shaderNormal, "mat");
		normalLengthNorm = gl.glGetUniformLocation(shaderNormal, "normalLength");
		animateNorm = gl.glGetUniformLocation(shaderNormal, "animateNormals");
		shapeNorm = gl.glGetUniformLocation(shaderNormal, "shape");
		timeNorm = gl.glGetUniformLocation(shaderNormal, "time");
		transparencyNorm = gl.glGetUniformLocation(shaderNormal, "transparency");


		cam = cam.withPosition(new Vec3D(5, 5, 2.5)).withAzimuth(Math.PI * 1.25).withZenith(Math.PI * -0.125);

		gl.glEnable(GL2GL3.GL_DEPTH_TEST);
	}

	void createBuffers(GL2GL3 gl) {

		float[] cube = {
				// bottom (z-) face
				1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, -1, 0, 1, 0, 0, 0, -1,
				// top (z+) face
				1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1,
				// x+ face
				1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0,
				// x- face
				0, 1, 0, -1, 0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 1, -1, 0, 0, 0, 0, 1, -1, 0, 0,
				// y+ face
				1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0,
				// y- face
				1, 0, 0, 0, -1, 0, 0, 0, 0, 0, -1, 0, 1, 0, 1, 0, -1, 0, 0, 0, 1, 0, -1, 0 };

		int[] indexBufferData = new int[36];
		for (int i = 0; i < 6; i++) {
			indexBufferData[i * 6] = i * 4;
			indexBufferData[i * 6 + 1] = i * 4 + 1;
			indexBufferData[i * 6 + 2] = i * 4 + 2;
			indexBufferData[i * 6 + 3] = i * 4 + 1;
			indexBufferData[i * 6 + 4] = i * 4 + 2;
			indexBufferData[i * 6 + 5] = i * 4 + 3;
		}
		OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 3),
				new OGLBuffers.Attrib("inNormal", 3) };

		this.cube = new OGLBuffers(gl, cube, attributes, indexBufferData);
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		grid = MeshGenerator.generateGrid(gl, meshSize, meshSize, "inParamPos");


		time += 0.001;

		gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, polygonMode);

		gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

		//******* GRID ***** //

		gl.glUseProgram(shaderGrid);
		gl.glUniformMatrix4fv(locMatGrid, 1, false, ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);

		if (isReflector == 1) {
			gl.glUniform3fv(locLightGrid, 1, ToFloatArray.convert(cam.getEye()), 0);
		} else {
			gl.glUniform3fv(locLightGrid, 1, ToFloatArray.convert(lightPos), 0);
		}

		gl.glUniform3fv(locEyeGrid, 1, ToFloatArray.convert(cam.getEye()), 0);

		normTex.bind(shaderGrid, "normTex", 1);

		gl.glUniform1f(locTimeGrid, time);
		gl.glUniform1i(isReflectorGrid, isReflector);
		gl.glUniform1i(lightCalcType, lightCalculationType);
		gl.glUniform1i(attenuationEnabled, attenuation);
		gl.glUniform1i(shapeNrGrid, shape);
		gl.glUniform1i(textureMapGrid, textureMap);
		gl.glUniform1i(textureTypeGrid, textureType);
		gl.glUniform1i(transparencyGrid, transparency);

		grid.draw(GL2GL3.GL_TRIANGLES, shaderGrid);

		gl.glUseProgram(shaderNormal);
		gl.glUniformMatrix4fv(locMatNormals, 1, false, ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
		gl.glUniform1f(normalLengthNorm, normalLength);
		gl.glUniform1i(animateNorm, animateNormals);
		gl.glUniform1i(shapeNorm, shape);
		gl.glUniform1f(timeNorm, time);
		gl.glUniform1i(transparencyNorm, transparency);

		grid.draw(GL2GL3.GL_TRIANGLES, shaderNormal);
		
		
		
		String text = new String(this.getClass().getName() + ": [LMB] camera, WSAD");

		textRenderer.drawStr2D(3, height - 20, text);
		textRenderer.drawStr2D(width - 90, 3, " (c) PGRF UHK");
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
		proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
		textRenderer.updateSize(width, height);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		cam = cam.addAzimuth(Math.PI * (ox - e.getX()) / width)
				.addZenith(Math.PI * (e.getY() - oy) / width);
		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			cam = cam.forward(0.1);
			break;
		case KeyEvent.VK_D:
			cam = cam.right(0.1);
			break;
		case KeyEvent.VK_S:
			cam = cam.backward(0.1);
			break;
		case KeyEvent.VK_A:
			cam = cam.left(0.1);
			break;
		case KeyEvent.VK_CONTROL:
			cam = cam.down(0.1);
			break;
		case KeyEvent.VK_SHIFT:
			cam = cam.up(0.1);
			break;
		case KeyEvent.VK_SPACE:
			cam = cam.withFirstPerson(!cam.getFirstPerson());
			break;
		case KeyEvent.VK_R:
			cam = cam.mulRadius(0.9f);
			break;
		case KeyEvent.VK_F:
			cam = cam.mulRadius(1.1f);
			break;
		case KeyEvent.VK_L:
			polygonMode = GL2GL3.GL_LINE;
			break;
		case KeyEvent.VK_P:
			polygonMode = GL2GL3.GL_FILL;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void dispose(GLAutoDrawable glDrawable) {
		glDrawable.getGL().getGL2GL3().glDeleteProgram(shaderGrid);
	}

	public int getShape() {
		return shape;
	}

	public void setShape(int shape) {
		this.shape = shape;
	}

	public int getIsReflector() {
		return isReflector;
	}

	public void setIsReflector(int isReflector) {
		this.isReflector = isReflector;
	}

	public int getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(int attenuation) {
		this.attenuation = attenuation;
	}

	public int getLightCalculationType() {
		return lightCalculationType;
	}

	public void setLightCalculationType(int lightCalculationType) {
		this.lightCalculationType = lightCalculationType;
	}

	public int getTextureMap() {
		return textureMap;
	}

	public void setTextureMap(int textureMap) {
		this.textureMap = textureMap;
	}

	public int getTextureType() {
		return textureType;
	}

	public void setTextureType(int textureType) {
		this.textureType = textureType;
	}

	public int getTransparency() {
		return transparency;
	}

	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}

	public float getNormalLength() {
		return normalLength;
	}

	public void setNormalLength(float normalLength) {
		this.normalLength = normalLength;
	}

	public int getAnimateNormals() {
		return animateNormals;
	}

	public void setAnimateNormals(int animateNormals) {
		this.animateNormals = animateNormals;
	}

	public int getMeshSize() {
		return meshSize;
	}

	public void setMeshSize(int meshSize) {
		this.meshSize = meshSize;
	}
}