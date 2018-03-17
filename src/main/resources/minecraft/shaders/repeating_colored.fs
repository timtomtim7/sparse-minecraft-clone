#version 330 core

uniform sampler2D uTexture;

in vec4 gColor;
in vec2 gTexCoord;
in vec4 gTexCoordRange;

out vec4 fColor;

void main() {
	vec2 texCoord = fract(gTexCoord) * gTexCoordRange.zw + gTexCoordRange.xy;

	fColor = texture2D(uTexture, texCoord) * gColor;
}