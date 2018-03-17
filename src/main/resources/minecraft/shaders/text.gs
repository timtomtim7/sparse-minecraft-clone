#version 330 core

const int MAX_CHARS = 8;

uniform vec4 uTexCoords[MAX_CHARS];
uniform float uWidths[MAX_CHARS];

uniform float uScale;
uniform float uPadding;
uniform bool uItalic;

uniform vec3 uColor;

uniform mat4 uViewProj;
uniform mat4 uModel;

uniform vec3 uOrigin;

layout(points) in;
layout(triangle_strip, max_vertices = 32) out; //MAX_CHARS * 4

out vec4 gColor;
out vec2 gTexCoord;

vec4 transform(vec2 vert) {
	vec4 pos = (vec4(uOrigin, 1) + vec4(vert * 8 * uScale, 0, 1)) ;
	return (pos * uModel) * uViewProj;
}

void main() {
	float offset = 0;

//	float italicOffset = if(uItalic) 2.0 / 8.0 else 0.0;
	float italicOffset = 0.0;
	if(uItalic)
		italicOffset = 2.0 / 8.0;

	gColor = vec4(uColor, 1.0);
	for(int i = 0; i < MAX_CHARS; i++) {
		vec4 texCoords = uTexCoords[i];
		float width = uWidths[i];

		gl_Position = transform(vec2(offset + 0, 0));
		gTexCoord = texCoords.xw;
		EmitVertex();

		gl_Position = transform(vec2(offset + 0 + italicOffset, 1));
		gTexCoord = texCoords.xy;
		EmitVertex();

		gl_Position = transform(vec2(offset + width, 0));
		gTexCoord = texCoords.zw;
		EmitVertex();

		gl_Position = transform(vec2(offset + width + italicOffset, 1));
		gTexCoord = texCoords.zy;
		EmitVertex();

		EndPrimitive();
		offset += width + uPadding;//+ (1.0 / 8.0);
	}
}