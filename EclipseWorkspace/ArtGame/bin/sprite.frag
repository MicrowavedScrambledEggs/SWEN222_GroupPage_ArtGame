#version 150 core

in vec2 uv;
out vec4 fragColor;

uniform sampler2D texture;

void main() {
	fragColor = texture( texture, uv);
}