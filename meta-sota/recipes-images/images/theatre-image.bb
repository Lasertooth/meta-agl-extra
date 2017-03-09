SUMMARY = "Release image for Theatre project"

require recipes-core/images/agl-image-minimal.inc

LICENSE = "CLOSED"

IMAGE_INSTALL_append = "\
    packagegroup-agl-image-minimal \
    rvi-sota-client \
    "
