#version 330 core

uniform vec3 uPosition;
uniform vec2 uSize;

uniform vec4 uColor;

uniform mat4 uViewProj;
uniform mat4 uModel;

layout(points) in;
layout(triangle_strip, max_vertices = 4) out;

out vec4 gColor;

vec4 transform(vec2 vert) {
	vec4 pos = vec4(uPosition, 1) + vec4(vert, 0, 1);
	return (pos * uModel) * uViewProj;
}

void main() {
	gColor = uColor;

	gl_Position = transform(vec2(0, 0));
	EmitVertex();

	gl_Position = transform(vec2(0, uSize.y));
	EmitVertex();

	gl_Position = transform(vec2(uSize.x, 0));
	EmitVertex();

	gl_Position = transform(vec2(uSize.x, uSize.y));
	EmitVertex();

	EndPrimitive();
}