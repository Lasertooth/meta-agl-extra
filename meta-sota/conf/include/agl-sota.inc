DISTRO_FEATURES_append = " agl-ota"
OVERRIDES .= ":sota"

IMAGE_INSTALL_append = " ostree"

# live image for OSTree-enabled systems
IMAGE_CLASSES += "image_types_ostree image_types_ota"
IMAGE_FSTYPES += "ostreepush otaimg"

# Please redefine OSTREE_REPO in order to have a persistent OSTree repo
OSTREE_REPO ?= "${DEPLOY_DIR_IMAGE}/ostree_repo"
OSTREE_BRANCHNAME ?= "agl-ota-${MACHINE}"
OSTREE_OSNAME ?= "agl"
OSTREE_INITRAMFS_IMAGE ?= "initramfs-ostree-image"

# See https://advancedtelematic.atlassian.net/browse/PRO-2693
PREFERRED_VERSION_linux-yocto_qemux86-64="4.4%"

# Should be moved to BSB layer in future
OSTREE_KERNEL_ARGS_qemux86-64 = "ramdisk_size=16384 rw rootfstype=ext4 rootwait rootdelay=2 ostree_root=/dev/hda1"
