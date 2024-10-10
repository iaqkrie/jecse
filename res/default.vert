#version 100

layout(location = 0) in vec2 aPos;
layout(location = 1) in vec4 aColor;

out vec4 vertexColor;

uniform mat3 matrix;

void main() {
    gl_Position = matrix * vec4(aPos, 0, 1);

    vertexColor = aColor;
}