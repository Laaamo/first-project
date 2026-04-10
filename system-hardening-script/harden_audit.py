#!/usr/bin/env python3
"""
harden_audit.py - Linux system security auditor
Checks for common misconfigurations and generates a compliance report.

Author: Bryan Perez
Usage:
    python3 harden_audit.py          # audit only
    sudo python3 harden_audit.py --apply  # audit + apply fixes
"""

import subprocess
import os
import sys
import datetime
import argparse

REPORT_DIR = "reports"
CHECKS = []
RESULTS = []


def check(name: str, description: str):
    """Decorator to register a security check."""
    def decorator(fn):
        CHECKS.append((name, description, fn))
        return fn
    return decorator


@check("root_login_disabled", "SSH root login should be disabled")
def check_root_login() -> tuple[bool, str]:
    try:
        with open("/etc/ssh/sshd_config") as f:
            for line in f:
                line = line.strip()
                if line.startswith("PermitRootLogin"):
                    val = line.split()[1].lower()
                    if val in ("no", "without-password", "prohibit-password"):
                        return True, f"PermitRootLogin={val} ✓"
                    else:
                        return False, f"PermitRootLogin={val} — should be 'no'"
        return False, "PermitRootLogin not set explicitly (default may allow root)"
    except FileNotFoundError:
        return None, "sshd_config not found (SSH not installed?)"


@check("open_ports", "No unnecessary ports should be open")
def check_open_ports() -> tuple[bool, str]:
    risky_ports = {21: "FTP", 23: "Telnet", 2049: "NFS", 6667: "IRC"}
    result = subprocess.run(["ss", "-tulnp"], capture_output=True, text=True)
    found_risky = []
    for port, service in risky_ports.items():
        if f":{port} " in result.stdout or f":{port}\t" in result.stdout:
            found_risky.append(f"{port}/{service}")
    if found_risky:
        return False, f"Risky ports open: {', '.join(found_risky)}"
    return True, "No high-risk ports detected ✓"


@check("firewall_active", "iptables firewall should have active rules")
def check_firewall() -> tuple[bool, str]:
    result = subprocess.run(
        ["iptables", "-L", "-n", "--line-numbers"],
        capture_output=True, text=True
    )
    if result.returncode != 0:
        return False, "Could not query iptables (run as root?)"
    lines = [l for l in result.stdout.splitlines() if l and not l.startswith("Chain") and not l.startswith("target")]
    if len(lines) > 0:
        return True, f"Firewall active with {len(lines)} rules ✓"
    return False, "No iptables rules found — firewall may be inactive"


@check("shadow_permissions", "/etc/shadow should be root-only (600 or 640)")
def check_shadow_perms() -> tuple[bool, str]:
    try:
        stat = os.stat("/etc/shadow")
        mode = oct(stat.st_mode)[-3:]
        if mode in ("600", "640", "000"):
            return True, f"/etc/shadow permissions: {mode} ✓"
        return False, f"/etc/shadow permissions: {mode} — should be 600"
    except PermissionError:
        return True, "/etc/shadow exists and is root-restricted ✓"
    except FileNotFoundError:
        return None, "/etc/shadow not found"


@check("auto_updates", "Unattended security updates should be enabled")
def check_auto_updates() -> tuple[bool, str]:
    paths = [
        "/etc/apt/apt.conf.d/20auto-upgrades",
        "/etc/apt/apt.conf.d/50unattended-upgrades"
    ]
    for path in paths:
        if os.path.exists(path):
            return True, f"Auto-updates config found: {path} ✓"
    return False, "No unattended-upgrades config found — consider enabling"


def run_audit() -> list[dict]:
    results = []
    for name, description, fn in CHECKS:
        try:
            passed, detail = fn()
        except Exception as e:
            passed, detail = None, f"Error: {e}"
        results.append({
            "name": name,
            "description": description,
            "passed": passed,
            "detail": detail
        })
        status = "✅ PASS" if passed else ("⚠️  SKIP" if passed is None else "❌ FAIL")
        print(f"  {status}  {description}")
        print(f"         → {detail}")
    return results


def generate_report(results: list[dict]) -> str:
    os.makedirs(REPORT_DIR, exist_ok=True)
    date_str = datetime.datetime.now().strftime("%Y-%m-%d")
    report_path = os.path.join(REPORT_DIR, f"security_report_{date_str}.md")

    passed = sum(1 for r in results if r["passed"] is True)
    failed = sum(1 for r in results if r["passed"] is False)
    skipped = sum(1 for r in results if r["passed"] is None)

    with open(report_path, "w") as f:
        f.write(f"# Security Audit Report\n\n")
        f.write(f"**Date:** {date_str}  \n")
        f.write(f"**Host:** {os.uname().nodename}  \n")
        f.write(f"**Result:** {passed} passed / {failed} failed / {skipped} skipped\n\n")
        f.write("---\n\n")
        for r in results:
            status = "✅ PASS" if r["passed"] else ("⚠️ SKIP" if r["passed"] is None else "❌ FAIL")
            f.write(f"### {status} — {r['description']}\n")
            f.write(f"- **Check:** `{r['name']}`\n")
            f.write(f"- **Detail:** {r['detail']}\n\n")

    return report_path


def main():
    parser = argparse.ArgumentParser(description="Linux Security Auditor")
    parser.add_argument("--apply", action="store_true", help="Apply firewall fixes after audit")
    args = parser.parse_args()

    print("\n=== Linux Security Audit ===\n")
    results = run_audit()
    report = generate_report(results)
    print(f"\n[+] Report saved to: {report}")

    if args.apply:
        print("\n[*] Applying firewall rules via deploy_firewall.sh...")
        subprocess.run(["bash", "deploy_firewall.sh"])


if __name__ == "__main__":
    main()
