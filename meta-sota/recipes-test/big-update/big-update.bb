DESCRIPTION = "Example Package with 50MB of content"
LICENSE = "CLOSED"

FILES_${PN} = "/usr/lib/big-update"

do_install() {
   install -d ${D}/usr/lib/big-update
   dd if=/dev/urandom of=${D}/usr/lib/big-update/a-big-file bs=1M count=50
}
