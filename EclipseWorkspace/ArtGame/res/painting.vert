#version 150 core
#extension GL_ARB_explicit_attrib_location : require

layout(location = 0) in vec3 squareVerts;
out vec2 uv;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
	
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(0.9*squareVerts + vec3(0, 0.5, -0.425), 1.0);
	uv = squareVerts.xy + vec2(0.5, 0.5);
}