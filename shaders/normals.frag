#version 150
in vec3 vertColor;

out vec4 outColor; // output from the fragment shader
uniform vec3 lightPos; // possibly n
uniform vec3 eyePos;
uniform int transparency;

void main() {
    
    if (transparency == 1){
        if (int((vertColor.x + vertColor.y) * 2) % 2 == 1)
            discard;
    }
    
	outColor = vec4(vertColor, 1.0);
} 
