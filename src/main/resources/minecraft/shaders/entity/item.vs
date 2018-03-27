#version 330 core

in vec3 aPosition;
in vec3 aNormal;
in vec2 aTexCoord;

out vec3 vNormal;
out vec2 vTexCoord;

uniform mat4 uModel;
uniform mat4 uViewProj;

void main() {
	vTexCoord = aTexCoord;
	vNormal = normalize((vec4(aNormal, 0.0) * uModel).xyz);
	gl_Position = (vec4(aPosition, 1) * uModel) * uViewProj;
}