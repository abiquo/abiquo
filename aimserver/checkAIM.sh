#!/bin/bash

#
# Abiquo community edition
# cloud management application for hybrid clouds
# Copyright (C) 2008-2010 - Abiquo Holdings S.L.
# 
# This application is free software; you can redistribute it and/or
# modify it under the terms of the GNU LESSER GENERAL PUBLIC
# LICENSE as published by the Free Software Foundation under
# version 3 of the License
# 
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# LESSER GENERAL PUBLIC LICENSE v.3 for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the
# Free Software Foundation, Inc., 59 Temple Place - Suite 330,
# Boston, MA 02111-1307, USA.
#

############################
### Script Configuration ###
############################

AIM_CONF=/etc/abiquo-aim.ini

################################
### End script configuration ###
################################

LANG="C"

function load_section() {
    eval `sed -e 's/[[:space:]]*\=[[:space:]]*/=/g' \
        -e 's/;.*$//' \
        -e 's/[[:space:]]*$//' \
        -e 's/^[[:space:]]*//' \
        -e "s/^\(.*\)=\([^\"']*\)$/\1=\"\2\"/" \
    < ${1} \
    | sed -n -e "/^\[${2}\]/,/^\s*\[/{/^[^;].*\=.*/p;}"`
}

function load_config() {
    load_section ${AIM_CONF} "monitor"
    load_section ${AIM_CONF} "rimp"
    load_section ${AIM_CONF} "vlan"
}

function check_proc() {
    echo -ne "Checking ${1}...\t"
    PID=`ps -e | grep ${1} | awk '{print $1}'`
    if [[ -n ${PID} ]]; then
	echo -n "OK (pid ${PID}, "
        PORT=`netstat -putln 2>/dev/null | grep "${1}" | head -n 1 | awk '{print $4}' | rev | cut -d: -f1 | rev`
        if [[ -n ${PORT} ]]; then
	    echo "listening at ${PORT})"
        else
            echo "not listening)"
        fi
    else
        echo "MISSING"
    fi
}

function check_file() {
    echo -ne "  Checking ${2}...\t"
    if [[ -e ${1} ]]; then
        echo "OK (${1})"
    else
        echo "MISSING"
    fi
}

function check_firewall() {
    echo "Checking firewall..."
    FWCONFIG=`chkconfig --list | grep iptables | awk '{print $2,$3,$4,$5,$6,$7}'`
    DEFAULTRL=`grep "initdefault" /etc/inittab | grep -v "^#" | cut -d: -f2`
    CURRENTRL=`runlevel | awk '{print $2}'`
    echo -ne "  Firewall status:\t"
    if [[ -n `echo ${FWCONFIG} | grep "${CURRENTRL}:off"` ]]; then
        echo -n "DISABLED, "
    else
        echo -n "ENABLED, "
    fi
    HAS_RULES=`iptables -nL | grep -iv ^chain | grep -iv ^target | grep -v ^$`
    if [[ -n ${HAS_RULES} ]]; then
        echo "active rules found!"
    else
        echo "no active rules"
    fi
    echo -e "  Runlevel config:\tcurrent = ${CURRENTRL}, default = ${DEFAULTRL}"
    echo -e "  Firewall activation:\t${FWCONFIG}"
    echo -ne "  SELinux status:\t"
    if [[ $(ls -A /selinux) ]]; then
        echo "ENABLED"
    else
        echo "DISABLED"
    fi
}

function check_rimp() {
    echo "  Checking rimp..."

    echo -ne "    Checking repository...\t"
    if [[ -d ${repository} ]]; then
        if [[ -f ${repository}/.abiquo_repository ]]; then
            echo "OK (at ${repository})"
        else
            echo "MISSING (${repository}/.abiquo_repository not found)"
        fi
    else
        echo "MISSING"
    fi

    echo -ne "    Checking datastore...\t"
    if [[ -d ${datastore} ]]; then
        echo "OK (at ${datastore})"
    else
        echo "MISSING"
    fi
}

function check_monitor() {
    echo -ne "  Checking monitor...\t"

    if [[ -n ${uri} ]]; then
        if [[ "${uri}" == "${1}" ]]; then
            echo "OK (uri = ${1})"
        else
            echo "ERROR (uri should be ${1})"
        fi
    else
        echo "MISSING"
    fi
}

function check_vlan() {
    echo "  Checking vlan..."
    echo -n "  " && check_file ${ifconfigCmd} "ifconfigCmd"
    echo -n "  " && check_file ${vconfigCmd} "vconfigCmd"
    echo -n "  " && check_file ${brctlCmd} "brctlCmd"
}

function check_aim() {
    echo "Checking AIM..."
    load_config

    echo -n "  "
    check_proc "abiquo-aim"
    check_rimp

    if [[ $# -gt 0 ]]; then
        if [[ "${1}" == "kvm" ]]; then
            check_monitor "qemu+unix:///system"
            check_file /usr/bin/qemu-kvm "emulator"
            check_file /usr/bin/qemu-kvm "loader"
        elif [[ "${1}" == "xen" ]]; then
            check_monitor "xen+unix:///"
            check_file /usr/lib64/xen/bin/qemu-dm "emulator"
            check_file /usr/lib/xen/boot/hvmloader "loader"
        fi
    fi

    check_vlan
}

check_proc "libvirtd"
check_firewall
check_aim $*
