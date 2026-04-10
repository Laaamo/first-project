#!/usr/bin/env python3
"""
network_scan.py - Automated network scanner for home lab monitoring
Runs Nmap scans, detects new/unknown devices, and logs findings.

Author: Bryan Perez
"""

import subprocess
import json
import os
import datetime
import socket

# Config
NETWORK_RANGE = "192.168.1.0/24"
KNOWN_DEVICES_FILE = "known_devices.json"
LOG_DIR = "logs"


def run_nmap_scan(target: str) -> str:
    """Run an Nmap ping scan and return raw output."""
    print(f"[*] Scanning {target}...")
    result = subprocess.run(
        ["nmap", "-sn", "-oG", "-", target],
        capture_output=True, text=True
    )
    return result.stdout


def parse_hosts(nmap_output: str) -> list[dict]:
    """Parse Nmap grepable output and extract live hosts."""
    hosts = []
    for line in nmap_output.splitlines():
        if "Host:" in line and "Status: Up" in line:
            parts = line.split()
            ip = parts[1]
            # Try reverse DNS
            try:
                hostname = socket.gethostbyaddr(ip)[0]
            except socket.herror:
                hostname = "unknown"
            hosts.append({"ip": ip, "hostname": hostname})
    return hosts


def load_known_devices() -> dict:
    """Load known devices from JSON file."""
    if os.path.exists(KNOWN_DEVICES_FILE):
        with open(KNOWN_DEVICES_FILE) as f:
            return json.load(f)
    return {}


def save_known_devices(devices: dict):
    """Save known devices to JSON file."""
    with open(KNOWN_DEVICES_FILE, "w") as f:
        json.dump(devices, f, indent=2)


def log_results(hosts: list[dict], unknown: list[dict]):
    """Write scan results to a timestamped log file."""
    os.makedirs(LOG_DIR, exist_ok=True)
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
    log_path = os.path.join(LOG_DIR, f"scan_{timestamp}.log")

    with open(log_path, "w") as f:
        f.write(f"Scan Time: {timestamp}\n")
        f.write(f"Total Hosts Found: {len(hosts)}\n")
        f.write(f"Unknown Devices: {len(unknown)}\n\n")
        f.write("--- All Hosts ---\n")
        for h in hosts:
            f.write(f"  {h['ip']:<18} {h['hostname']}\n")
        if unknown:
            f.write("\n--- ⚠️  UNKNOWN DEVICES ---\n")
            for h in unknown:
                f.write(f"  {h['ip']:<18} {h['hostname']}\n")

    print(f"[+] Results logged to {log_path}")
    return log_path


def main():
    raw = run_nmap_scan(NETWORK_RANGE)
    hosts = parse_hosts(raw)
    known = load_known_devices()

    unknown = []
    for host in hosts:
        if host["ip"] not in known:
            print(f"[!] Unknown device detected: {host['ip']} ({host['hostname']})")
            unknown.append(host)
            # Add to known so we don't re-alert next run
            known[host["ip"]] = host["hostname"]

    save_known_devices(known)
    log_results(hosts, unknown)

    print(f"\n[+] Scan complete. {len(hosts)} hosts found, {len(unknown)} unknown.")


if __name__ == "__main__":
    main()
