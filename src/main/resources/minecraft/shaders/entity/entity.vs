#version 330 core

const int MAX_BONE_COUNT = 16;

in vec3 aPosition;
in vec2 aTexCoord;
in vec3 aNormal;
in int aBoneIndex;

out vec2 vTexCoord;
out vec3 vNormal;

uniform mat4 uBoneTransforms[MAX_BONE_COUNT];
uniform mat4 uModel;
uniform mat4 uViewProj;

void main() {
	vec4 pos = vec4(aPosition, 1.0);
	pos *= uBoneTransforms[aBoneIndex];
	pos *= uModel;
	pos *= uViewProj;

//	gl_Position = (vec4(aPosition, 1) * uModel) * uViewProj;

	vec4 normal = vec4(aNormal, 0.0);
	normal *= uBoneTransforms[aBoneIndex];
	normal *= uModel;

	vTexCoord = aTexCoord;
	vNormal = normalize(normal.xyz);

	gl_Position = pos;
}