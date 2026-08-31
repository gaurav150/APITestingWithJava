# Dockerfile
# Builds a WireMock standalone image with your project's stub mappings baked in.
#
# Build:
#   docker build -t my-wiremock:latest .
#
# Run standalone (without compose):
#   docker run -p 8181:8080 my-wiremock:latest

FROM wiremock/wiremock:4.0.0-beta.38

# WireMock's default working directory inside this official image
WORKDIR /home/wiremock

# Bake in your stub mapping files and any __files (bodyFileName references)
COPY wiremock/mappings/ /home/wiremock/mappings/
COPY wiremock/__files/ /home/wiremock/__files/

# Default WireMock port inside the container
EXPOSE 8080

# --verbose is optional; remove if you don't want request logging noise
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["--global-response-templating", "--verbose"]
