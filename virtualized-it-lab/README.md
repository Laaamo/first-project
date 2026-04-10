# 🖥️ Virtualized IT Lab Environment

**Duration:** Oct 2024 — Present  
**Stack:** VirtualBox, Windows Server 2022, Active Directory, Ubuntu Server, Linux

## Overview

A multi-VM home lab that simulates a real enterprise IT environment. Built to practice system administration, Active Directory management, and network troubleshooting in a safe, isolated space.

## Lab Topology

```
Host Machine (VirtualBox)
├── DC01 — Windows Server 2022 (Domain Controller)
│   ├── Active Directory Domain Services
│   ├── DNS Server
│   └── DHCP Server
├── SRV01 — Ubuntu Server 22.04
│   └── File sharing / test workload
├── WS01 — Windows 10 Client (domain-joined)
└── [Internal Network + NAT adapters]
```

## What I Configured

### Active Directory
- Promoted Windows Server 2022 to domain controller for `lab.local`
- Created OUs for Users, Computers, and Groups
- Configured Group Policies (password policies, login banners, software restrictions)
- Managed user accounts, security groups, and role-based access

### Networking
- Configured VirtualBox internal network + NAT for internet access
- Set up DHCP scope on the DC for lab subnet
- Configured DNS forward/reverse lookup zones
- Implemented network segmentation between VMs using virtual switches

### Troubleshooting Practice
- Used `ping`, `traceroute`, `netstat`, `nslookup` for connectivity issues
- Reviewed Windows Event Viewer for login failures and service errors
- Diagnosed DHCP lease and DNS resolution problems

## Key Takeaways

- Deep understanding of how Active Directory structures enterprise identity
- Hands-on with Windows Server administration tools (Server Manager, RSAT, ADUC)
- Real troubleshooting workflow: symptoms → logs → network → config
