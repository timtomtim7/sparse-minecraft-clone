#version 330 core

in vec3 aPosition;
in vec2 aTexCoord;
in vec3 aNormal;
in float aBrightness;
in vec3 aColor;
in vec4 aLight;

out vec3 vPosition;
out vec2 vTexCoord;
out vec3 vNormal;
out float vBrightness;
out vec3 vColor;
out vec3 vLight;

uniform mat4 uModel;
uniform mat4 uViewProj;
uniform vec3 uAmbientLight;

void main() {
	gl_Position = (vec4(aPosition / 16.0, 1.0) * uModel) * uViewProj;

	vPosition = aPosition;
	vTexCoord = aTexCoord;
	vNormal = aNormal;
	vBrightness = aBrightness;
	vColor = aColor;
	vLight = mix(aLight.xyz, uAmbientLight, aLight.w);
}