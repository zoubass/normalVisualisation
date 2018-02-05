package oglutils;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GL3;

import java.io.*;
import java.util.ArrayList;

public final class ShaderUtils {

	public static final String VERTEX_SHADER_EXTENSION = ".vert";
	public static final String FRAGMENT_SHADER_EXTENSION = ".frag";
	public static final String GEOMETRY_SHADER_EXTENSION = ".geom";
	public static final String TESS_CONTROL_SHADER_EXTENSION = ".tesc";
	public static final String TESS_EVALUATION_SHADER_EXTENSION = ".tese";
	public static final String COMPUTE_SHADER_EXTENSION = ".comp";

	public static final int VERTEX_SHADER_SUPPORT_VERSION = 120;
	public static final int FRAGMENT_SHADER_SUPPORT_VERSION = 120;
	public static final int GEOMETRY_SHADER_SUPPORT_VERSION = 150;
	public static final int TESSELATION_SUPPORT_VERSION = 400;
	public static final int COMPUTE_SHADER_SUPPORT_VERSION = 430;

	private static final String[] SHADER_FILE_EXTENSIONS = { VERTEX_SHADER_EXTENSION, FRAGMENT_SHADER_EXTENSION,
			GEOMETRY_SHADER_EXTENSION, TESS_CONTROL_SHADER_EXTENSION, TESS_EVALUATION_SHADER_EXTENSION,
			COMPUTE_SHADER_EXTENSION };

	private static final int[] SHADER_SUPPORT_EXTENSIONS = { VERTEX_SHADER_SUPPORT_VERSION,
			FRAGMENT_SHADER_SUPPORT_VERSION, GEOMETRY_SHADER_SUPPORT_VERSION, TESSELATION_SUPPORT_VERSION,
			TESSELATION_SUPPORT_VERSION, COMPUTE_SHADER_SUPPORT_VERSION };

	private static final int[] SHADER_NAME_CONSTANTS = { GL2GL3.GL_VERTEX_SHADER, GL2GL3.GL_FRAGMENT_SHADER,
			GL3.GL_GEOMETRY_SHADER, GL3.GL_TESS_CONTROL_SHADER, GL3.GL_TESS_EVALUATION_SHADER, GL3.GL_COMPUTE_SHADER };

	private static final String[] SHADER_NAMES = { "Vertex", "Fragment", "Geometry", "Control", "Evaluation",
			"Compute" };

	/**
	 * Load, create, compile, attach and link shader sources defined as files
	 * 
	 * @param gl
	 *            GL context
	 * @param vertexShaderFileName
	 *            full path name of vertex shader file with/without file
	 *            extension (VERTEX_SHADER_EXTENSION) or null
	 * @param fragmentShaderFileName
	 *            full path name of fragment shader file with/without file
	 *            extension (FRAGMENT_SHADER_EXTENSION) or null
	 * @param geometryShaderFileName
	 *            full path name of geometry shader file with/without file
	 *            extension (GEOMETRY_SHADER_EXTENSION) or null
	 * @param tessControlShaderFileName
	 *            full path name of control shader file with/without file
	 *            extension (TESS_CONTROL_SHADER_EXTENSION) or null
	 * @param tessEvaluationShaderFileName
	 *            full path name of evaluation shader file with/without file
	 *            extension (TESS_EVALUATION_SHADER_EXTENSION) or null
	 * @param computeShaderFileName
	 *            full path name of compute shader file with/without file
	 *            extension (COMPUTE_SHADER_EXTENSION) or null
	 * @return new id of shader program
	 */
	public static int loadProgram(GL2GL3 gl, String vertexShaderFileName, String fragmentShaderFileName,
			String geometryShaderFileName, String tessControlShaderFileName, String tessEvaluationShaderFileName,
			String computeShaderFileName) {
		String[] shaderFileNames = new String[SHADER_FILE_EXTENSIONS.length];
		shaderFileNames[0] = vertexShaderFileName;
		shaderFileNames[1] = fragmentShaderFileName;
		shaderFileNames[2] = geometryShaderFileName;
		shaderFileNames[3] = tessControlShaderFileName;
		shaderFileNames[4] = tessEvaluationShaderFileName;
		shaderFileNames[5] = computeShaderFileName;
		return loadProgram(gl, shaderFileNames);
	}

	/**
	 * Load, create, compile, attach and link shader sources defined as files
	 * 
	 * @param gl
	 *            GL context
	 * @param shaderFileName
	 *            full path name of shader file without file extension
	 * @return new id of shader program
	 */
	public static int loadProgram(GL2GL3 gl, String shaderFileName) {
		String[] shaderFileNames = new String[SHADER_FILE_EXTENSIONS.length];
		for (int i = 0; i < SHADER_FILE_EXTENSIONS.length; i++)
			shaderFileNames[i] = shaderFileName;
		return loadProgram(gl, shaderFileNames);
	}

	/**
	 * Load, create, compile, attach and link shader sources defined as files
	 * 
	 * @param gl
	 *            GL context
	 * @param shaderFileNames
	 *            array of full path name of shader files with/without file
	 *            extension in order vertex, fragment, geometry, control,
	 *            evaluation and compute shader or null
	 * 
	 * @return new id of shader program
	 */
	public static int loadProgram(GL2GL3 gl, String[] shaderFileNames) {
		if (shaderFileNames.length > SHADER_NAMES.length) {
			System.err.println("Number of shader sources is bigger than number of shaders");
			return -1;
		}
		String[][] shaderSrcArray = new String[SHADER_FILE_EXTENSIONS.length][];
		for (int i = 0; i < shaderFileNames.length; i++) {
			if (shaderFileNames[i] == null)
				continue;

			String shaderFileName = shaderFileNames[i];
			int index = shaderFileNames[i].indexOf(".");
			if (index < 0) // file extension added
				shaderFileName += SHADER_FILE_EXTENSIONS[i];

			System.out.print("Shader file: " + shaderFileName + " Reading ... ");

			String[] shaderSrc = readShaderProgram(shaderFileName);
			if (shaderSrc == null) {
				continue;
			} else {
				System.out.println("OK");
			}
			shaderSrcArray[i] = shaderSrc;
		}
		;
		return loadProgram(gl, shaderSrcArray);
	}

	/**
	 * Load, create, compile, attach and link shader sources defined as arrays
	 * of Strings
	 * 
	 * @param gl
	 *            GL context
	 * @param vertexShaderSrc
	 *            array of Strings with GLSL code for vertex shader or null
	 * @param fragmentShaderSrc
	 *            array of Strings with GLSL code for fragment shader or null
	 * @param geometryShaderSrc
	 *            array of Strings with GLSL code for geometry shader or null
	 * @param tessControlShaderSrc
	 *            array of Strings with GLSL code for control shader or null
	 * @param tessEvaluationShaderSrc
	 *            array of Strings with GLSL code for evaluation shader or null
	 * @param computeShaderSrc
	 *            array of Strings with GLSL code for compute shader or null
	 * @return new id of shader program
	 */
	public static int loadProgram(GL2GL3 gl, String[] vertexShaderSrc, String[] fragmentShaderSrc,
			String[] geometryShaderSrc, String[] tessControlShaderSrc, String[] tessEvaluationShaderSrc,
			String[] computeShaderSrc) {
		String[][] shaderSrcArray = new String[SHADER_FILE_EXTENSIONS.length][];
		shaderSrcArray[0] = vertexShaderSrc;
		shaderSrcArray[1] = fragmentShaderSrc;
		shaderSrcArray[2] = geometryShaderSrc;
		shaderSrcArray[3] = tessControlShaderSrc;
		shaderSrcArray[4] = tessEvaluationShaderSrc;
		shaderSrcArray[5] = computeShaderSrc;
		return loadProgram(gl, shaderSrcArray);
	}

	/**
	 * Load, create, compile, attach and link shader sources defined as arrays
	 * of Strings
	 * 
	 * @param gl
	 *            GL context
	 * @param shaderSrcArray
	 *            array of arrays of Strings with GLSL codes for shaders in
	 *            order vertex, fragment, geometry, control, evaluation and
	 *            compute shader or null
	 * @return new id of shader program
	 */
	public static int loadProgram(GL2GL3 gl, String[][] shaderSrcArray) {
		OGLUtils.emptyGLError(gl);
		if (shaderSrcArray.length > SHADER_NAMES.length) {
			System.err.println("Number of shader sources is bigger than number of shaders");
			return -1;
		}

		int shaderProgram = gl.glCreateProgram();
		if (shaderProgram < 0) {
			System.err.println("Unable create new shader program ");
			return -1;
		}
		System.out.println("New shader program '" + shaderProgram + "' created");

		int[] shaders = new int[shaderSrcArray.length];
		for (int i = 0; i < shaderSrcArray.length; i++) {
			shaders[i] = 0;
			if (shaderSrcArray[i] == null)
				continue;
			System.out.print("  " + SHADER_FILE_EXTENSIONS[i].substring(1).toUpperCase() + " shader: Creating ... ");
			if (OGLUtils.getVersionGLSL(gl) < SHADER_SUPPORT_EXTENSIONS[i]) {
				System.err.println(SHADER_NAMES[i] + " shader extension is not supported by OpenGL driver ("
						+ OGLUtils.getVersionGLSL(gl) + ").");
				continue;
			}
			shaders[i] = createShaderProgram(gl, shaderSrcArray[i], SHADER_NAME_CONSTANTS[i]);
			if (shaders[i] > 0) {
				System.out.print("'" + shaders[i] + "' OK,  ");
			} else {
				System.err.println("Shader is not supported");
				continue;
			}

			System.out.print("Compiling '" + shaders[i] + "'... ");
			shaders[i] = compileShaderProgram(gl, shaders[i]);
			if (shaders[i] > 0) {
				System.out.print("OK, ");
			} else {
				// Don't leak shaders either
				gl.glDeleteShader(shaders[i]);
				return (-1);
			}

			System.out.print("Attaching '" + shaders[i] + "' to '" + shaderProgram + "' ... ");
			gl.glAttachShader(shaderProgram, shaders[i]);
			System.out.println("OK, ");
		}
		
		System.out.print("  Linking shader program '" + shaderProgram + "' ... ");
		if (linkProgram(gl, shaderProgram)) {
			System.out.println("OK");
		} else {
			// We don't need the program anymore
			gl.glDeleteProgram(shaderProgram);
		}

		for (int shader : shaders) {
			if (shader > 0) {
				// Always detach shaders after a successful link
				gl.glDetachShader(shaderProgram, shader);
				// Don't leak shader either
				gl.glDeleteShader(shader);
			}
		}

		return shaderProgram;
	}

	/**
	 * Read shader code as stream from file
	 * 
	 * @param streamFileName
	 *            full path name to a shader file
	 * @return array of Strings with GLSL shader code
	 */
	static public String[] readShaderProgram(String streamFileName) {

		InputStream is = ShaderUtils.class.getResourceAsStream(streamFileName);
		if (is == null) {
			System.out.println("File not found ");
			return null;
		}

		BufferedReader brv = null;
		try {
			brv = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		String line;
		ArrayList<String> shader = new ArrayList<>();
		try {
			while ((line = brv.readLine()) != null) {
				shader.add(new String(line));
			}
			is.close();
			brv.close();
		} catch (IOException e) {
			System.err.println("Read error in ");
			e.printStackTrace();
		}

		String[] result = new String[shader.size()];
		return shader.toArray(result);
	}

	/**
	 * Create shader and define source as array of Strings. At the end of a
	 * String of code line is char \n added. Chars after // are deleted.
	 * 
	 * @param gl
	 *            GL context
	 * @param shaderSrc
	 *            array of Strings with GLSL shader code
	 * @param type
	 *            of shader
	 * @return new id of shader
	 */
	static public int createShaderProgram(GL2GL3 gl, String[] shaderSrc, int type) {
		int shader = gl.glCreateShader(type);
		if (shader <= 0) {
			return shader;
		}

		for (int i = 0; i < shaderSrc.length; i++) {
			String line = shaderSrc[i];
			int index = line.indexOf("//");
			if (index > 0)
				line = line.substring(0, index);
			index = line.indexOf("\n");
			if (index < 0)
				line = new String(line + "\n");
			shaderSrc[i] = line;
		}

		gl.glShaderSource(shader, shaderSrc.length, shaderSrc, (int[]) null, 0);

		return shader;
	}

	/**
	 * Compile shader
	 * 
	 * @param gl
	 *            GL context
	 * @param shader
	 *            id of shader
	 * @return new id of shader
	 */
	static public int compileShaderProgram(GL2GL3 gl, int shader) {
		String error;

		gl.glCompileShader(shader);
		error = checkLogInfo(gl, shader, GL2GL3.GL_COMPILE_STATUS);
		if (error == null) {
			return shader;
		} else {
			System.err.println("failed");
			System.err.println("\n" + error);
			if (shader > 0)
				gl.glDeleteShader(shader);
			return -1;
		}

	}

	/**
	 * Link shader program
	 * 
	 * @param gl
	 *            GL context
	 * @param shader
	 *            id of shader program
	 * @return new id of shader program
	 */
	static public boolean linkProgram(GL2GL3 gl, int shaderProgram) {
		String error;
		gl.glLinkProgram(shaderProgram);
		error = checkLogInfo(gl, shaderProgram, GL2GL3.GL_LINK_STATUS);
		if (error == null) {
			return true;
		} else {
			System.err.println("failed");
			System.err.println("\n" + error);
			return false;
		}
	}

	static private String checkLogInfo(GL2GL3 gl, int programObject, int mode) {
		switch (mode) {
		case GL2GL3.GL_COMPILE_STATUS:
			return checkLogInfoShader(gl, programObject, mode);
		case GL2GL3.GL_LINK_STATUS:
		case GL2GL3.GL_VALIDATE_STATUS:
			return checkLogInfoProgram(gl, programObject, mode);
		default:
			return "Unsupported mode.";
		}
	}

	static private String checkLogInfoShader(GL2GL3 gl, int programObject, int mode) {
		int[] error = new int[] { -1 };
		gl.glGetShaderiv(programObject, mode, error, 0);
		if (error[0] != GL2GL3.GL_TRUE) {
			int[] len = new int[1];
			gl.glGetShaderiv(programObject, GL2GL3.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] == 0) {
				return null;
			}
			byte[] errorMessage = new byte[len[0]];
			gl.glGetShaderInfoLog(programObject, len[0], len, 0, errorMessage, 0);
			return new String(errorMessage, 0, len[0]);
		}
		return null;
	}

	static private String checkLogInfoProgram(GL2GL3 gl, int programObject, int mode) {
		int[] error = new int[] { -1 };
		gl.glGetProgramiv(programObject, mode, error, 0);
		if (error[0] != GL2GL3.GL_TRUE) {
			int[] len = new int[1];
			gl.glGetProgramiv(programObject, GL2GL3.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] == 0) {
				return null;
			}
			byte[] errorMessage = new byte[len[0]];
			gl.glGetProgramInfoLog(programObject, len[0], len, 0, errorMessage, 0);
			return new String(errorMessage, 0, len[0]);
		}
		return null;
	}

}
