#version 330 core

uniform float currentTime;

void main() {
    float sinv = (sin(currentTime) + 1.0) / 2.0;
    float cosv = (cos(currentTime) + 1.0) / 2.0;

    gl_FragColor = vec4(sinv, cosv, 1.0, 1.0);
}
