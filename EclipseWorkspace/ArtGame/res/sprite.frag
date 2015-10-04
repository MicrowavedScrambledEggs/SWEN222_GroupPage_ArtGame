#version 150 core
#extension GL_ARB_explicit_attrib_location : require
in vec2 uv;
out vec4 fragColor;

uniform sampler2D sprite;

void main() {
	fragColor = texture(sprite, uv);
	//fragColor = vec4(1.0, 0.5, 0.0, 1.0);
}