#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;

uniform mat4 u_model;
uniform mat4 u_view;
uniform mat4 u_projection;

out vec2 v_texCoord;
out vec3 v_normal;

void main () {
	gl_Position = u_projection * u_view * u_model * vec4(aPosition, 1.0);
	v_texCoord = aTexCoord;
	v_normal = mat3(u_model) * aNormal;
}
