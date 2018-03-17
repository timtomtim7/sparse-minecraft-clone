#version 330 core

uniform mat4 uModel;
uniform mat4 uViewProj;

in uint aNothing;

void main() {
	gl_Position = vec4(0, 0, 0, 1) * uModel * uViewProj;
}