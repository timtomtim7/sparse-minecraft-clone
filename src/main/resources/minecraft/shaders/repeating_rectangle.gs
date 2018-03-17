#version 330 core

//uniform vec4 uTexCoords;
uniform vec4 uTexCoordRange;
uniform vec4 uTexCoords;
uniform vec4 uPadding; // left, right, bottom, top

uniform vec3 uPosition;
uniform vec2 uSize;

uniform vec4 uColor;

uniform mat4 uViewProj;
uniform mat4 uModel;

layout(points) in;
layout(triangle_strip, max_vertices = 4) out;

out vec4 gColor;
out vec2 gTexCoord;
out vec4 gTexCoordRange;

vec4 transform(vec2 vert) {
	vec4 pos = vec4(uPosition, 1) + vec4(vert, 0, 1);
	return (pos * uModel) * uViewProj;
}

void main() {
	gColor = uColor;

	gTexCoordRange = vec4(uTexCoordRange.xy, uTexCoordRange.zw - uTexCoordRange.xy);
//	gTexCoordRange = uTexCoords;

//	vec2 min = uTexCoords.xy;
//	vec2 max = uTexCoords.zw;
//
//	float xPadding = uPadding.x + uPadding.z;
//	float yPadding = uPadding.y + uPadding.w;
//


	gl_Position = transform(vec2(0, 0));
	gTexCoord = uTexCoords.xw;
//	gTexCoord = vec2(0, 1);
	EmitVertex();

	gl_Position = transform(vec2(0, uSize.y));
	gTexCoord = uTexCoords.xy;
//	gTexCoord = vec2(0, 0);
	EmitVertex();

	gl_Position = transform(vec2(uSize.x, 0));
	gTexCoord = uTexCoords.zw;
//	gTexCoord = vec2(1, 1);
	EmitVertex();

	gl_Position = transform(vec2(uSize.x, uSize.y));
	gTexCoord = uTexCoords.zy;
//	gTexCoord = vec2(1, 0);
	EmitVertex();

	EndPrimitive();
}