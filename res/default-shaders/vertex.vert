#version 330

layout(location = 0) in vec2 aPos;
layout(location = 1) in vec4 aColor;

out vec4 vertexColor;

uniform mat3 matrix;

void main() {
    vec3 tPos = matrix * vec3(aPos, 1);
    gl_Position = vec4(tPos, 1);

    vertexColor = aColor;
}
