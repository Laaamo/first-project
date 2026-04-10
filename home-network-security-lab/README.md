# 🔐 Home Network Security Lab

**Duration:** Dec 2024 — Present  
**Stack:** Raspberry Pi 4, Kali Linux, Wireshark, Nmap, OpenWrt, Python, Bash

## Overview

A personal cybersecurity lab built on a Raspberry Pi 4 running Kali Linux. Used for hands-on penetration testing practice, traffic analysis, and network hardening — all on an isolated home network segment.

## Setup

### Hardware
- Raspberry Pi 4 (4GB RAM) — monitoring station
- Home router flashed with OpenWrt firmware
- Isolated guest VLAN for test devices

### Software
- **Kali Linux** — primary OS for pen testing tools
- **Wireshark** — packet capture and traffic analysis
- **Nmap** — network scanning and vulnerability identification
- **OpenWrt** — custom router firmware with firewall rules

## What I Did

1. **Network Monitoring** — Configured Wireshark to capture and analyze HTTP/HTTPS traffic on the LAN
2. **Vulnerability Scanning** — Ran Nmap scans to map devices, open ports, and potential attack surfaces
3. **Router Hardening** — Installed OpenWrt, set up isolated guest VLAN, wrote custom iptables firewall rules
4. **Automation** — Built Python/Bash scripts to run daily scans and alert on unknown devices
5. **Reporting** — Documented findings and remediation steps after each scan cycle

## Scripts

See [`scripts/`](./scripts/) for:
- `network_scan.py` — automated Nmap scan with device fingerprinting
- `scan_report.sh` — shell wrapper that logs results with timestamps

## Key Takeaways

- Real-world experience with packet analysis and network reconnaissance
- Learned how attackers enumerate networks; applied that to defensive hardening
- OpenWrt firewall rules gave me hands-on iptables experience in a live environment
