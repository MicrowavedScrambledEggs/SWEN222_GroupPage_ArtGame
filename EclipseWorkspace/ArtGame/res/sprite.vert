#version 150 core
#extension GL_ARB_explicit_attrib_location : require

layout(location = 0) in vec3 squareVerts;
out vec2 uv;

uniform vec3 cameraUp;
uniform vec3 cameraRight;
uniform vec3 position;
uniform mat4 view;
uniform mat4 projection;

void main() {
	vec3 vertPos = position + cameraRight * squareVerts.x + cameraUp * squareVerts.y;
    mat4 vp = projection * view;
    gl_Position = vp * vec4(vertPos, 1.0);
	uv = squareVerts.xy + vec2(0.5, 0.5);
}