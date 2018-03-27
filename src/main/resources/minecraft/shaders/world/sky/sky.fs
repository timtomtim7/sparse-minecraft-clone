#version 330 core

uniform vec3 uSunDirection;
//uniform vec3 uSunsetDirection;
uniform vec3 uGravity;
//uniform vec3 uCamera;

uniform float uTime;

in vec3 vPosition;

out vec4 fColor;

void main(){
	vec3 dir = normalize(vPosition);

//	float horizonPosition = clamp(dot(dir, uGravity) * 0.5 + 0.5 - 0.05 - (uCamera.y / 512), 0.001, 0.999);
//	float sunsetAmt = clamp(dot(dir, uSunPerp), 0, 1)
//	vec3 horizon = texture2D(uHorizonGradient, vec2(0, horizonPosition)).rgb;

	float horizonPos = clamp(dot(dir, uGravity)+1, 0, 1);
	horizonPos = clamp(pow(horizonPos + 0.1, 2.5), 0, 1);

	vec3 nightBackground = vec3(0.007843, 0.007843, 0.0156862);
	vec3 nightBelowHorizon = vec3(0.035294, 0.043137, 0.074509) - nightBackground;

	vec3 dayBackground = vec3(0.494117, 0.662745, 1);
	vec3 dayBelowHorizon = vec3(0.701960, 0.811764, 1) - dayBackground;

	vec3 nightHorizon = nightBackground + (nightBelowHorizon * horizonPos);
	vec3 dayHorizon = dayBackground + (dayBelowHorizon * horizonPos);

	float time = dot(uSunDirection, uGravity) * 0.5 + 0.5;
	float dayNightBlendAmt = clamp(time / 2, -1, 1);

	vec3 horizon = mix(dayHorizon, nightHorizon, dayNightBlendAmt);

//	float sunsetAmt = clamp(dot(dir, uSunsetDirection)+1, 0, 1);
//	sunsetAmt = clamp(pow(sunsetAmt + 0.1, 2.5), 0, 1);
//	sunsetAmt = sunsetAmt * time;

//	vec3 sunset = (vec3(0.5019, 0.0745, 0.11372) - mix(dayBackground, nightBackground, 0.5)) * sunsetAmt;

//	fColor = vec4(horizon + sunset, 0.0);
	fColor = vec4(horizon, 1.0);
}