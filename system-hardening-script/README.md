# 🛡️ Automated System Hardening Script

**Duration:** Sep 2024  
**Stack:** Python, Bash, Linux Security, iptables

## Overview

A Python + Bash toolkit that audits Linux systems against security best practices and automates firewall hardening. Generates a compliance report after each run. Tested on Kali, Ubuntu, and Debian.

## Features

- ✅ Checks root login status (SSH)
- ✅ Lists open ports and flags risky ones
- ✅ Verifies firewall (iptables) is active
- ✅ Checks for unattended upgrades / auto-updates
- ✅ Validates file permissions on sensitive files (`/etc/passwd`, `/etc/shadow`)
- ✅ Deploys hardened iptables rules automatically
- ✅ Generates a markdown compliance report

## Usage

```bash
# Audit only (no changes)
python3 harden_audit.py

# Audit + apply iptables firewall rules
sudo bash deploy_firewall.sh

# Full run: audit + harden + report
sudo python3 harden_audit.py --apply
```

## Output

Generates a `security_report_YYYY-MM-DD.md` with pass/fail status for each check and recommended remediation steps.

## Tested On

- Kali Linux 2024.x
- Ubuntu 22.04 LTS
- Debian 12 (Bookworm)
