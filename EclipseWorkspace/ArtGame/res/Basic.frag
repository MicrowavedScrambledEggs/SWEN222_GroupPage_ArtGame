#version 150 core
#extension GL_ARB_explicit_attrib_location : require

in vec3 vertexColor;
out vec4 fragColor;
void main() {
    fragColor = vec4(vertexColor, 1.0);
}