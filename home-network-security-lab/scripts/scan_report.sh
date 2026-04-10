#!/bin/bash
# scan_report.sh - Shell wrapper for daily network scans
# Logs output with timestamp; designed for cron job use
#
# Cron example (daily at 3am):
#   0 3 * * * /path/to/scan_report.sh >> /var/log/netscan.log 2>&1

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')
LOG_FILE="$SCRIPT_DIR/../logs/daily_$(date '+%Y-%m-%d').log"

echo "============================================"
echo " Network Scan Report - $TIMESTAMP"
echo "============================================"

# Run Python scanner
python3 "$SCRIPT_DIR/network_scan.py" 2>&1 | tee -a "$LOG_FILE"

# Optional: full port scan on subnet (slower, use sparingly)
# echo "[*] Running port scan..."
# nmap -sV --open 192.168.1.0/24 -oN "$SCRIPT_DIR/../logs/portscan_$(date '+%Y-%m-%d').txt"

echo ""
echo "[✓] Done at $(date '+%H:%M:%S')"
