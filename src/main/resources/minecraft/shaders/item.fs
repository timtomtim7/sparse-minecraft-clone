#version 330 core

const float PI = 3.14159265;
const float ROTATION_RADIANS = (45.0 / 180.0) * PI;
const float SIN_45 = sin(ROTATION_RADIANS);
const float COS_45 = cos(ROTATION_RADIANS);
const mat2 ROTATION_MATRIX = mat2(COS_45, SIN_45, -SIN_45, COS_45);
const mat2 INV_ROTATION_MATRIX = ROTATION_MATRIX * ROTATION_MATRIX * ROTATION_MATRIX;
const mat2 ROTATION_MATRIX_NEGATIVE = mat2(SIN_45, COS_45, -COS_45, SIN_45);

uniform sampler2D uTexture;

uniform sampler2D uEnchantTexture;
uniform float uEnchantTime;
uniform vec3 uEnchantColor;

uniform vec3 uColor;
uniform vec3 uLightDirection;

in vec3 vNormal;
in vec2 vTexCoord;

out vec4 fColor;

void main() {
	vec4 color = texture2D(uTexture, vTexCoord);
	if(color.a < 0.5)
		discard;

	vec2 rotatedCoord1 = (vTexCoord * ROTATION_MATRIX) * 8 + uEnchantTime;
	vec2 rotatedCoord2 = (vTexCoord * INV_ROTATION_MATRIX) * 8 + uEnchantTime + 0.5;

//	vec2 rotatedCoord2 = (vTexCoord * ROTATION_MATRIX_NEGATIVE) * 8 + uEnchantTime;
//	vec4 enchantColor = (texture2D(uEnchantTexture, rotatedCoord1) + texture2D(uEnchantTexture, rotatedCoord2)) * vec4(uEnchantColor, 1.0);
//	vec4 enchantColor = texture2D(uEnchantTexture, rotatedCoord1) * vec4(uEnchantColor, 1.0);
	vec4 enchantColor = mix(texture2D(uEnchantTexture, rotatedCoord1), texture2D(uEnchantTexture, rotatedCoord2), 0.5) * vec4(uEnchantColor, 1.0);
//	color += enchantColor;

	float brightness = dot(vNormal, uLightDirection) * 0.5 + 0.5;
	brightness = brightness * 0.6 + 0.4;

	fColor = ((color * vec4(uColor, 1.0)) + enchantColor) * brightness;
}