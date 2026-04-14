## 🔥 Real-World Scenario: "The website works on my phone but not on my laptop"

### The Problem

> You're at a coffee shop. You open your laptop and try to visit `app.example.com`.
> The browser shows **"This site can't be reached"** or **ERR_NAME_NOT_RESOLVED**.
>
> You pull out your phone (on the same Wi-Fi) and open the same URL — it loads perfectly.
>
> You try a different website on your laptop — `google.com` works fine.
>
> **What's going on?**

---

### Clue gathering

| Test | Laptop | Phone |
|------|--------|-------|
| `google.com` | ✅ Works | ✅ Works |
| `app.example.com` | ❌ Fails | ✅ Works |
| Same Wi-Fi? | Yes | Yes |
| VPN? | No | No |

Both devices are on the same network, so it's not a firewall or ISP issue. Google works on the laptop, so DNS isn't completely broken.

**The problem is specific to one domain on one device.**

---

### Step 1: Check the laptop's DNS cache

bash
# What does the laptop think the IP is?
nslookup app.example.com

**Result:**

Server:  192.168.1.1
Address: 192.168.1.1

Name:    app.example.com
Address: 93.184.216.34       ← This is an OLD IP!

The company migrated `app.example.com` to a new server last night.

- **New IP:** `104.21.55.12` (what the phone resolves)
- **Old IP:** `93.184.216.34` (what the laptop cached)

The old server is decommissioned → connection refused → site can't be reached.

---

### Step 2: Why does the phone work?


Phone:
 Opened the app for the first time today
 → No cached DNS → Fresh lookup → Gets new IP 104.21.55.12 ✅

Laptop:
 Visited the site yesterday
 → OS cached the old DNS record → Still using old IP 93.184.216.34 ❌
 → Old server is dead → Connection fails

The phone has no stale cache. The laptop does.

---

### Step 3: But wait — shouldn't the cache expire (TTL)?

Yes! But here's what went wrong:


Old DNS record:    app.example.com → 93.184.216.34   TTL: 86400 (24 hours!)
New DNS record:    app.example.com → 104.21.55.12    TTL: 300  (5 minutes)

The admin set a **24-hour TTL** on the old record. When they updated the IP, devices that already cached it won't check again for up to 24 hours.

**Timeline:**


Monday 10:00 AM  — You visit app.example.com on laptop
                   Laptop caches: 93.184.216.34 (TTL 24h, expires Tuesday 10 AM)

Monday 11:00 PM  — Admin changes DNS to 104.21.55.12

Tuesday 8:00 AM  — You open laptop at coffee shop
                   Laptop still has cached old IP (doesn't expire until 10 AM)
                   → Tries to connect to dead server → FAILS

Tuesday 8:00 AM  — You open phone (never visited before)
                   Phone does fresh lookup → Gets new IP → WORKS

---

### Step 4: The browser adds another caching layer

Even after the OS cache expires, the **browser has its own DNS cache**:


Layer 1: Browser DNS cache     (Chrome caches for up to 60s)
Layer 2: OS DNS cache          (follows TTL, can be hours)
Layer 3: Router DNS cache      (varies by router)
Layer 4: ISP resolver cache    (follows TTL)

So even flushing the OS cache might not help if the browser is holding onto it.

---

### The Fix

**Immediate fix (as a user):**

bash
1. Flush OS DNS cache (macOS)
sudo dscacheutil -flushcache
sudo killall -HUP mDNSResponder

 2. Flush browser DNS cache
 Chrome: navigate to
chrome://net-internals/#dns → Click "Clear host cache"

3. Verify fresh resolution
nslookup app.example.com
Should now return: 104.21.55.12 ✅

**Proper fix (as the engineer who caused this):**

| Step | When | What to do |
|------|------|-----------|
| **Before migration** | 24–48 hours early | Lower TTL from `86400` to `300` (5 min) |
| **Wait for old TTL to expire** | 24 hours | All caches worldwide now have the short TTL |
| **Change the DNS record** | Migration time | Point to new IP — propagates in ~5 minutes |
| **Verify** | After change | Test from multiple locations using `dig` or online tools |
| **After migration stabilizes** | Days later | Optionally raise TTL back to `3600` or higher |


The correct migration timeline:
```
Day 1: TTL 86400 → Lower to 300
       ├── Wait 24h for old TTL to expire everywhere
Day 2: Change IP  93.184.216.34 → 104.21.55.12
       ├── All caches refresh within 5 minutes
       ├── Zero downtime ✅
Day 5: Raise TTL back to 3600 (optional)
```
---

### Why this matters in system design

This scenario tests understanding of:

| Concept | How it applies |
|---------|---------------|
| **TTL planning** | High TTL = fast performance but slow failover. Must lower TTL *before* any DNS change |
| **Multi-layer caching** | Browser, OS, router, ISP — each layer can serve stale data independently |
| **DNS propagation** | There's no "instant" DNS update — it's eventual consistency across the internet |
| **Migration planning** | DNS changes need a preparation window, not just a record update |
| **Debugging methodology** | Compare working vs broken device → isolate the variable → check each cache layer |

---
