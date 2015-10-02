#version 150 core
#extension GL_ARB_explicit_attrib_location : require

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec3 normal;
out vec3 vertexColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 light;
uniform vec3 matColor;

void main() {
	float cosTheta = clamp(dot(normal, light), 0, 1);
	
	vertexColor = clamp (0.25 * matColor + matColor * cosTheta, 0, 1);
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}