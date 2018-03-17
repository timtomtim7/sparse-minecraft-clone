#version 330 core

uniform sampler2D uTexture;

in vec4 gColor;
in vec2 gTexCoord;

out vec4 fColor;

void main() {
	fColor = texture2D(uTexture, gTexCoord) * gColor;
}