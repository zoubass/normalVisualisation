#version 150
layout(triangles) in;

layout(line_strip, max_vertices=8) out;

uniform float normalLength;
uniform mat4 mat;
uniform float time;
uniform int animateNormals;
uniform int shape;

in Vertex {
  vec3 normal;
  vec3 color;
  vec3 position; 
} vertex[];

out vec3 vertColor;

void calculateTriangleNormals(float normLength){
 vec3 P0 = vertex[0].position;
 vec3 P1 = vertex[1].position;
 vec3 P2 = vertex[2].position;
 
 vec3 V0 = P0 - P1;
 vec3 V1 = P2 - P1;
 vec3 N;
 
 if (shape == 5 || shape == 3 || shape==4) {
    N = cross(V1, V0);
 } else {
    N = cross(V0, V1);
 }
 
 N = normalize(N);
 
 // Center of the triangle
 vec3 P = (P0+P1+P2) / 3.0;
 
 gl_Position = mat * vec4(P, 1.0);
 vertColor = vec3(0, 1.0, 0);
 EmitVertex();
 
 gl_Position = mat * vec4(P + N * normLength, 1.0);
 vertColor = vec3(0, 1.0, 0);
 EmitVertex();
 EndPrimitive();
}

void calculateVertexNorms(float normLength) {
  int i;
    for(i=0; i<gl_in.length(); i++) {
      vec3 P = vertex[i].position;
      vec3 N = normalize(vertex[i].normal.xyz);
      

      gl_Position = mat * vec4(P, 1.0);
      vertColor = vec3(1.0, 0, 0);
      EmitVertex();
    
      gl_Position = mat * vec4(P + N * normLength, 1.0);
      vertColor = vec3(1.0, 0, 0);
      EmitVertex();
      EndPrimitive();

      gl_Position = gl_in[i].gl_Position + 0.02;
      vertColor = vertex[i].color;
      EmitVertex();

      EndPrimitive();
    }
}

void main() {
  
  float normLength = normalLength;
  
  if(animateNormals == 1) {
     normLength = time - (0.3 * floor(time/0.3));
  }

  calculateTriangleNormals(normLength);

  calculateVertexNorms(normLength);
  
}
