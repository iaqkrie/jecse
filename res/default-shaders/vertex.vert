#version 330

layout(location = 0) in vec2 aPos;
layout(location = 1) in vec4 aColor;

out vec4 vertexColor;

uniform mat3 model;
uniform mat3 view;
uniform mat3 projection;

void main() {
    vec3 tPos = projection * view * model * vec3(aPos, 1);
    gl_Position = vec4(tPos.xy, 0, 1);

    vertexColor = aColor;
}
