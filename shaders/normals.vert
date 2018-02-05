#version 150
in vec2 inParamPos;
in vec3 inPosition;
in vec3 inNormal;

out Vertex{
  vec3 normal;
  vec3 color;
  vec3 position;
} vertex;

uniform mat4 mat;
uniform vec3 lightPos;
uniform vec3 eyePos;

uniform int shape;
uniform float time;


const float PI = 3.1415926536;

vec3 plane(vec2 paramPos) {
    return vec3(10 * paramPos, 0);
}

vec3 planeNormal(vec2 paramPos) {
    return vec3(0,0, 1);
}

vec3 rag(vec2 paramPos) {
    float z = sin(10 * ((paramPos.x * paramPos.x) * mod(time, 2) + paramPos.y * paramPos.y))/10;
    return vec3(2 * paramPos, z);
}

vec3 sphere(vec2 paramPos) {
    float a = 2 * PI * paramPos.x;
    float z = PI * paramPos.y;
    return vec3(
        cos(a) * sin(z),
        sin(a) * sin(z),
        cos(z)
    );
}

vec3 bean(vec2 paramPos){
    float a = 2 * PI * paramPos.x;
    float z = PI * paramPos.y;
    return vec3(
     cos(a)*sin(z),
     2*sin(a)*sin(z),
     3*cos(z)
    );
}

vec3 sphereNormal(vec2 paramPos) {
    return sphere(paramPos);
}

vec3 cone(vec2 paramPos) {
    float a = 2 * PI * paramPos.x;
    return vec3(
        cos(a) * paramPos.y,
        sin(a) * paramPos.y,
        (1 - paramPos.y)
    );
}


vec3 cylinder(vec2 paramPos) {
    float a = 2 * PI * paramPos.x;
    return vec3(
        cos(a),
        sin(a),
        (1 - paramPos.y)
    );
}

vec3 diabolo(vec2 paramPos) {
    float a = 2 * PI * paramPos.x;
    float z = paramPos.y * PI - PI/2.0;
        return vec3(
            z * cos(a),
            z * sin(a),
            2 * sin(z) / 2
    );
}

vec3 coneNormal(vec2 paramPos) {
    float a = 2 * PI * paramPos.x;
    vec3 tx = vec3(
        -sin(a) * paramPos.y * 2 * PI,
        cos(a) * paramPos.y * 2 * PI,
        0
    );
    vec3 ty = vec3(
        cos(a),
        sin(a),
        -1
    );
    return cross(ty, tx);
}

vec3 surface(vec2 paramPos) {
    return sphere(paramPos);
}

vec3 surfaceRag(vec2 paramPos) {
    return rag(paramPos);
}

vec3 normal(vec2 paramPos) {
    float d = 1e-5;
    vec2 dx = vec2(d, 0);
    vec2 dy = vec2(0, d);
    vec3 tx = (surface(paramPos + dx) - surface(paramPos - dx)) / (2 * d);
    vec3 ty = (surface(paramPos + dy) - surface(paramPos - dy)) / (2 * d);
    return cross(ty, tx);
}


vec3 normalRag(vec2 paramPos) {
    float d = 1e-5;
    vec2 dx = vec2(d, 0);
    vec2 dy = vec2(0, d);
    vec3 tx = (surfaceRag(paramPos + dx) - surfaceRag(paramPos - dx)) / (2 * d);
    vec3 ty = (surfaceRag(paramPos + dy) - surfaceRag(paramPos - dy)) / (2 * d);
    return cross(tx, ty);
}

vec3 normalCylinder(vec2 paramPos) {
    float d = 1e-5;
    vec2 dx = vec2(d, 0);
    vec2 dy = vec2(0, d);
    vec3 tx = (cylinder(paramPos + dx) - cylinder(paramPos - dx)) / (2 * d);
    vec3 ty = (cylinder(paramPos + dy) - cylinder(paramPos - dy)) / (2 * d);
    return cross(ty, tx);
}


vec3 normalDiabolo(vec2 paramPos) {
    float d = 1e-5;
    vec2 dx = vec2(d, 0);
    vec2 dy = vec2(0, d);
    vec3 tx = (diabolo(paramPos + dx) - diabolo(paramPos - dx)) / (2 * d);
    vec3 ty = (diabolo(paramPos + dy) - diabolo(paramPos - dy)) / (2 * d);
    return cross(tx, ty);
}

mat3 tangentMat(vec2 paramPos) {
    float d = 1e-5;
    vec2 dx = vec2(d, 0);
    vec2 dy = vec2(0, d);
    vec3 tx = (surface(paramPos + dx) - surface(paramPos - dx)) / (2 * d);
    vec3 ty = (surface(paramPos + dy) - surface(paramPos - dy)) / (2 * d);
    vec3 x = normalize(tx);
    vec3 y = normalize(-ty);
    vec3 z = cross(x, y);
    x = cross(y, z);
    return mat3(x,y,z);
}

void main() {


if (shape == 0) {
    vertex.position = sphere(inParamPos);
    vertex.normal = sphereNormal(inParamPos);
}
if (shape == 1) {
    vertex.position = cone(inParamPos);
    vertex.normal = coneNormal(inParamPos);
}
if (shape == 2) {
    vertex.position = cylinder(inParamPos);
    vertex.normal = normalCylinder(inParamPos);
}
if (shape == 3) {
    vertex.position = plane(inParamPos);
    vertex.normal = planeNormal(inParamPos);
}
if (shape == 4) {
    vertex.position = diabolo(inParamPos);
    vertex.normal = normalDiabolo(inParamPos);
}
if (shape == 5) {
    vertex.position = rag(inParamPos);
    vertex.normal = normalRag(inParamPos);
}
if (shape == 6) {
    vertex.position = bean(inParamPos);
    vertex.normal = normal(inParamPos);
}
   	        
//    vertex.normal = normal(inParamPos);
    
//    vertex.position = sphere(inParamPos);
//	vertex.normal = sphereNormal(inParamPos);
    vertex.color =  vec3(1.0, 1.0, 0.0);
    gl_Position = mat * vec4(vertex.position, 1.0);
}
