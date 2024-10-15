#version 330

in vec2 texCoord;

uniform vec4 color;
uniform sampler2D tex;

void main() {
    gl_FragColor = color * texture(tex, texCoord);
}
