#version 330 core

in vec2 v_texCoord;
in vec3 v_normal;

uniform sampler2D u_texture;
uniform vec4 u_color;
uniform vec3 u_lightDirection;
uniform vec4 u_lightColor;
uniform vec4 u_ambientColor;
uniform bool u_lit;

void main() {
	vec4 texColor = texture(u_texture, v_texCoord);
	vec4 baseColor = texColor * u_color;

	if (u_lit) {
		vec3 normal = normalize(v_normal);
		float diffuse = max(dot(normal, normalize(-u_lightDirection)), 0.0);
		vec4 lighting = u_ambientColor + u_lightColor * diffuse;
		gl_FragColor = baseColor * lighting;
	} else {
		gl_FragColor = baseColor;
	}
}
