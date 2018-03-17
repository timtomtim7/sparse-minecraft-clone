#version 330 core

in vec3 aPosition;
in vec2 aTexCoord;

out vec2 vTexCoord;

uniform mat4 uModel;
uniform mat4 uProjection;
uniform mat4 uRotation;

uniform float uScale;

void main() {
	vTexCoord = aTexCoord;

	gl_Position = (((vec4(aPosition.xy * uScale, aPosition.z, 1) * uModel) * uRotation) * uProjection);
}