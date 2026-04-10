#!/bin/bash
# deploy_firewall.sh - Automated iptables firewall hardening
# Run as root. Applies a sensible default ruleset for a Linux workstation/server.
#
# Author: Bryan Perez

set -e

echo "[*] Flushing existing rules..."
iptables -F
iptables -X
iptables -Z

echo "[*] Setting default policies (DROP all inbound/forward)..."
iptables -P INPUT DROP
iptables -P FORWARD DROP
iptables -P OUTPUT ACCEPT

echo "[*] Allowing loopback..."
iptables -A INPUT -i lo -j ACCEPT
iptables -A OUTPUT -o lo -j ACCEPT

echo "[*] Allowing established/related connections..."
iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT

echo "[*] Allowing SSH (port 22)..."
iptables -A INPUT -p tcp --dport 22 -m conntrack --ctstate NEW -j ACCEPT

echo "[*] Allowing ICMP (ping)..."
iptables -A INPUT -p icmp --icmp-type echo-request -j ACCEPT

echo "[*] Dropping invalid packets..."
iptables -A INPUT -m conntrack --ctstate INVALID -j DROP

echo "[*] Logging dropped packets (rate-limited)..."
iptables -A INPUT -m limit --limit 5/min -j LOG --log-prefix "iptables DROP: " --log-level 7

echo ""
echo "[+] Firewall rules applied:"
iptables -L -v -n --line-numbers

# Persist rules (Debian/Ubuntu)
if command -v iptables-save &>/dev/null; then
    iptables-save > /etc/iptables/rules.v4 2>/dev/null || \
    iptables-save > /tmp/iptables-rules-$(date +%Y%m%d).txt
    echo "[+] Rules saved."
fi

echo "[✓] Firewall deployment complete."
