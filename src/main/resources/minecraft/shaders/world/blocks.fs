#version 330 core

uniform sampler2D uTexture;
//uniform vec3 uLightDirection;

in vec3 vPosition;
in vec2 vTexCoord;
in vec3 vNormal;
in float vBrightness;
in vec3 vColor;

out vec4 fColor;

const float LOG2 = -1.442695;
const float FOG_DENSITY = 0.01;

float fog() {
	float d = FOG_DENSITY * (gl_FragCoord.z / gl_FragCoord.w);
	return 1.0 - clamp(exp2(d * d * LOG2), 0.0, 1.0);
}

void main() {
//	float brightness = dot(vNormal, uLightDirection) * 0.5 + 0.5;
//	brightness = brightness * 0.6 + 0.4;

	vec4 texColor = texture2D(uTexture, vTexCoord);
	vec4 shadowedColor = vec4(texColor.rgb * vColor * vBrightness, texColor.a);
	vec4 fogColor =  vec4(0.701960, 0.811764, 1, 1.0);

	fColor = mix(shadowedColor, fogColor, fog());

	//TODO: Transparency is probably a bit confused by this
//	fColor = texture2D(uTexture, vTexCoord) * vec4(vColor, 1.0) * brightness;
}
