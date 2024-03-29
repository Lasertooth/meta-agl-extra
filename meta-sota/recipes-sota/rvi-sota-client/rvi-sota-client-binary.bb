DESCRIPTION = "sota-client rust recipe (binary)"
HOMEPAGE = "https://github.com/advancedtelematic/rvi_sota_client"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea"

EXTERNALSRC = "${THISDIR}/../../../../rvi_sota_client"

inherit externalsrc systemd

FILES_${PN} = " \
                /lib64 \
                ${bindir}/sota_client \
                ${bindir}/sota_sysinfo.sh \
                ${bindir}/system_info.sh \
                ${bindir}/sota_ostree.sh \
                ${bindir}/sota_prov.sh \
                ${sysconfdir}/sota_client.version \
                ${sysconfdir}/sota_certificates \
                /var/sota/sota_provisioning_credentials.p12 \
                /var/sota/sota_provisioning_url.env \
                ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/sota_client_autoprovision.service', '', d)} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/sota_client_auto.service', '', d)} \
              "
SYSTEMD_SERVICE_${PN} = "sota_client.service"

RCONFLICTS_${PN} = "rvi-sota-client"

DEPENDS += " openssl-native "

RDEPENDS_${PN} = " libcrypto \
                   libssl \
                   dbus \
                   bash \
                   lshw \
                   jq \
                   curl \
                   python \
                   python-canonicaljson \
                   "

export SOTA_AUTOPROVISION_CREDENTIALS
export SOTA_AUTOPROVISION_URL

do_install() {
  install -d ${D}${bindir}
  install -m 0755 ${S}/run/sota_client ${D}${bindir}
  install -m 0755 ${S}/run/sota_sysinfo.sh ${D}${bindir}
  ln -fs ${bindir}/sota_sysinfo.sh ${D}${bindir}/system_info.sh  # For compatibilty with old sota.toml files
  install -m 0755 ${S}/run/sota_ostree.sh ${D}${bindir}
  install -m 0755 ${S}/run/sota_prov.sh ${D}${bindir}

  if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
    install -d ${D}/${systemd_unitdir}/system
    install -c ${S}/run/sota_client_ostree_auto.service ${D}${systemd_unitdir}/system/sota_client.service
  fi

  install -d ${D}${sysconfdir}
  echo `git log -1 --pretty=format:%H` > ${D}${sysconfdir}/sota_client.version
  install -c ${S}/run/sota_certificates ${D}${sysconfdir}
  ln -fs /lib ${D}/lib64

  if [ -n "$SOTA_AUTOPROVISION_CREDENTIALS" ]; then
    EXPDATE=`openssl pkcs12 -in $SOTA_AUTOPROVISION_CREDENTIALS -password "pass:" -nodes 2>/dev/null | openssl x509 -noout -enddate | cut -f2 -d "="`

    if [ `date +%s` -ge `date -d "${EXPDATE}" +%s` ]; then
      bberror "Certificate ${SOTA_AUTOPROVISION_CREDENTIALS} has expired on ${EXPDATE}"
    fi

    install -d ${D}/var
    install -d ${D}/var/sota
    install -m 0655 $SOTA_AUTOPROVISION_CREDENTIALS ${D}/var/sota/sota_provisioning_credentials.p12
    echo "SOTA_GATEWAY_URI=$SOTA_AUTOPROVISION_URL" > ${D}/var/sota/sota_provisioning_url.env
    install -c ${S}/run/sota_client_autoprovision.service ${D}${systemd_unitdir}/system/sota_client_autoprovision.service
  fi

}
