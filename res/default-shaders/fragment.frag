#version 330

in vec2 texCoord;

uniform sampler2D tex;

void main() {
    gl_FragColor = texture(tex, texCoord);
}
