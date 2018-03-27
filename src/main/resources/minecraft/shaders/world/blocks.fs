#version 330 core

uniform sampler2D uTexture;
uniform vec3 uLightDirection;

in vec3 vPosition;
in vec2 vTexCoord;
in vec3 vNormal;
in vec3 vColor;

out vec4 fColor;

void main() {
	float brightness = dot(vNormal, uLightDirection) * 0.5 + 0.5;
	brightness = brightness * 0.6 + 0.4;

	//TODO: Transparency is probably a bit confused by this
	fColor = texture2D(uTexture, vTexCoord) * vec4(vColor, 1.0) * brightness;
}
