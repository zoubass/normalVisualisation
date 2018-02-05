#version 150
in vec3 vertColor;
in vec3 vertNormal;
in vec3 vertPosition;
in vec3 eyeVec;
in vec3 lightVec;
in float lightDistance;

in vec3 ambiComponentVert;
in vec3 difComponentVert;
in vec3 specComponentVert;

out vec4 outColor;
uniform vec3 lightPos;
uniform vec3 eyePos;
uniform sampler2D normTex;
uniform int isReflector;
uniform int lightCalcType;
uniform int attenuationEnabled;
uniform int textureMap;
uniform int textureType;
uniform int transparency;

float spotCutOffInner = 0.8;
float spotCutOffOuter = 0.79;
vec3 spotDirection = vec3(0.0,0.0,-1.0);

float constantAttenuation = 0.05;
float linearAttenuation= 0.05;
float quadraticAttenuation= 0.05;

vec3 ambiComponent;
vec3 difComponent;
vec3 specComponent;
vec3 lVec;  

float scaleL = 0.02;
float scaleK = -0.02;

void main() {
    
if (transparency == 1){
    if (int((vertColor.x + vertColor.y) * 2) % 2 == 1)
        discard;
}

vec3 lVec = normalize(lightVec);
vec3 inNormal;

if (textureType == 1 || textureType == 2 || textureType == 3) {
    if (textureType == 1) {
            inNormal = vertNormal;
            outColor = vec4(normalize(vertNormal) * 0.5 + 0.5, 1.0);
    }
        
    if (textureType == 2) {
            inNormal = vertNormal;
            outColor = vec4(normalize(vertPosition),1.0);
    }
    if (textureType == 3) {
            inNormal = vertNormal;
            outColor = vec4(0.4, 0.3, 1.0, 1.0);
    }
} else {
    if (lightCalcType == 1 ) {
        
        vec3 matDifCol = vec3(0.5, 0.5, 0.5);
        vec3 matSpecCol = vec3(1);
        vec3 ambientLightCol = vec3(0.3, 0.1, 0.3);
        vec3 directLightCol = vec3(1.0, 0.9, 0.9); // possibly n
           // /better use uniforms
       
        vec2 texCoord = vertColor.xy * vec2(1,-1) + vec2(0,1);
        vec4 textColor;
        
        if (textureType == 0) {
           // paralax mapping
           if (textureMap == 1) {
               float height = texture(normTex, texCoord).r;
               height = height * scaleL + scaleK;
               vec3 eye = normalize(eyeVec);
               vec2 offset = eye.xy * height;
                   
               inNormal = texture(normTex, texCoord+offset).xyz * 2 - 1;
               textColor = texture(normTex,texCoord+offset);
           } else {
            //normal mapping
               vec2 texCoord = vertColor.xy * vec2(1,-1) + vec2(0,1);
               inNormal = texture(normTex, texCoord).xyz * 2 - 1;
               textColor = texture(normTex,texCoord);
           }
       }
        vec3 lVec = normalize(lightVec);
        vec3 normInNormal = normalize(inNormal);
       
        ambiComponent = ambientLightCol * matDifCol * textColor.xyz;
    
        float difCoef = pow(max(0, lVec.z), 0.7) * max(0, dot(normInNormal, lVec));
        
        difComponent = directLightCol * difCoef * matDifCol * textColor.xyz;
           
        vec3 reflected = reflect(-lVec, normInNormal);
        float specCoef = pow(max(0, lVec.z), 0.7) * pow(max(0,
             dot(normalize(eyeVec), reflected)
                ), 70);
        specComponent = directLightCol * matSpecCol * specCoef;
    } else {
        ambiComponent = ambiComponentVert;
        difComponent = difComponentVert;
        specComponent = specComponentVert;
    }

    float att = 1.0 / (constantAttenuation + linearAttenuation * lightDistance + quadraticAttenuation * lightDistance * lightDistance);

    float spotEffect = dot(normalize(spotDirection),normalize(-lVec));


    if (isReflector == 1) {
    
        if (spotEffect > spotCutOffInner) {
            if (attenuationEnabled == 1) {
                outColor = vec4(ambiComponent + att * (difComponent + specComponent), 1.0);
            } else {
                outColor = vec4(ambiComponent + (difComponent + specComponent), 1.0);
            }
                        
        } else {    
            outColor = vec4(ambiComponent, 1.0);
        }
        if(spotEffect > spotCutOffOuter){
         float grad = (spotEffect - spotCutOffOuter)/(spotCutOffInner - spotCutOffOuter);
            if (attenuationEnabled == 1) {
                outColor = vec4(ambiComponent + grad*att*(difComponent + specComponent), 1.0);
            } else {
                outColor = vec4(ambiComponent + grad*(difComponent + specComponent), 1.0);
            }
                
        } else {
            outColor = vec4(ambiComponent, 1.0);
        }
        
    } else {
        if (attenuationEnabled == 1) {
            outColor = vec4(ambiComponent + att*(difComponent + specComponent), 1.0);
        } else {
            outColor = vec4(ambiComponent + (difComponent + specComponent), 1.0);
        }
    }

}

}
