#version 330

uniform vec3 uStart;
uniform vec3 uEnd;

uniform mat4 uViewProj;

layout(points) in;
layout(line_strip, max_vertices = 2) out;

void main() {
	gl_Position = vec4(uStart, 1.0) * uViewProj;
	EmitVertex();

	gl_Position = vec4(uEnd, 1.0) * uViewProj;
	EmitVertex();

	EndPrimitive();
}
