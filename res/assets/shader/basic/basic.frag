#version 330 core

in vec2 v_texCoord;

uniform sampler2D u_texture;
uniform vec4 u_color;

void main() {
	vec4 texColor = texture(u_texture, v_texCoord);

	gl_FragColor = texColor * u_color;
}
