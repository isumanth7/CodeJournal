# DNS Resolution Deep Dive: Interactive FAQ Guide

**Date:** April 14, 2026
**Author:** Sai Sumanth
**Topic:** Networking — DNS, VPN, and DoH

> **Learning Goal:** Understand what happens when you type `google.com` in your browser
---

## 🌐 What is the Internet?

Think of the internet as a giant city with millions of buildings (websites). Each building has:

- A **street address** (IP address like `142.250.190.46`) — this is what computers use
- A **name** (like "Google Tower" or `google.com`) — this is what humans remember

Just like you can't find a building without knowing its address, computers can't connect to websites without knowing their IP address!

---
## 📞 What is DNS? (The Internet's Phone Book)

Imagine you want to call your friend, but you only know their name, not their phone number.

**You look them up in your phone's contacts!**

DNS works exactly like that:

1. You know the website's name → `google.com`
2. DNS looks up the "phone number" → `142.250.190.46`
3. Your computer can now "call" (connect to) the website!

### How it actually works under the hood

When you type `google.com`, a chain of lookups happens:


Your Browser
 → checks its own memory (browser cache)
 → asks your OS (operating system cache)
 → asks your router
 → asks your ISP's DNS server (Recursive Resolver)
 → asks the Root Name Server ("Who handles .com?")
 → asks the TLD Name Server ("Who handles google.com?")
 → asks Google's Authoritative Name Server ("What's the IP?")
 → returns 142.250.190.46
 → your browser connects!

Each step only happens if the previous one doesn't already know the answer. This is called **recursive resolution**.

### Key DNS Record Types

| Record | What it does | Example |
|--------|-------------|---------|
| **A** | Maps domain → IPv4 address | `google.com → 142.250.190.46` |
| **AAAA** | Maps domain → IPv6 address | `google.com → 2404:6800:4007:...` |
| **CNAME** | Alias pointing to another domain | `www.google.com → google.com` |
| **MX** | Mail server for the domain | `google.com → smtp.google.com` |
| **NS** | Which name server is authoritative | `google.com → ns1.google.com` |
| **TTL** | How long to cache the answer | `300` (seconds) |

---

## 🏃 Why Do We Need Fast DNS?

Imagine if every time you wanted to call your friend, you had to:

1. Ask someone for their number
2. Wait for them to search through a huge phone book
3. Write down the number
4. Then finally make the call

**That would be painfully slow!**

That's why your phone saves contacts — this is **caching**.
DNS does the same thing. It remembers website addresses so you don't have to look them up every single time.

### Where does caching happen?

| Layer | What it caches | How long |
|-------|---------------|----------|
| **Browser** | Recently visited sites | Minutes |
| **Operating System** | All apps' DNS lookups | Minutes to hours |
| **Router** | All devices on your network | Hours |
| **ISP DNS Server** | Millions of lookups from all customers | Based on TTL |

### Try it yourself
```
bash
# See your OS DNS cache (macOS)
sudo dscacheutil -flushcache   # clears it
nslookup google.com            # does a fresh lookup

# See how long a record is cached
dig google.com | grep -i ttl
```
---

## 🔐 What is a VPN? (Your Internet Disguise)

Imagine you're sending a letter to your friend:

**Without VPN:**
- You write a letter
- Put it in a clear plastic envelope (anyone can read it)
- Mail it directly from your house
- Everyone knows it's from you and can read what you wrote

**With VPN:**
- You write a letter
- Put it in a **locked box** (encrypted)
- Send it to a trusted friend in another city (VPN server)
- Your friend opens the box and mails it from *their* city
- It looks like the letter came from your friend's city, not yours!

### Why use a VPN?

| Benefit | What it means |
|---------|--------------|
| **Privacy** | Your internet traffic is encrypted — no one in between can read it |
| **Location masking** | Websites see the VPN server's location, not yours |
| **Access content** | Reach services available in other regions |
| **Public Wi-Fi safety** | Protects you on coffee shop / airport networks |

### What happens to DNS when you use a VPN?

This is where it gets interesting:

- **Without VPN:** Your DNS queries go to your ISP → they can see every site you visit
- **With VPN:** Your DNS queries go through the VPN tunnel → your ISP sees nothing

⚠️ **DNS Leak:** Sometimes your DNS queries escape the VPN tunnel and still go to your ISP. This defeats the purpose of the VPN. Good VPN apps have "DNS leak protection" to prevent this.

---
## 🛡️ What is DoH? (DNS over HTTPS)

Normal DNS is like shouting your question in a crowded room — everyone nearby can hear which websites you're looking up.

**DoH (DNS over HTTPS)** is like whispering your question into an encrypted phone call. Same question, same answer — but nobody can eavesdrop.

| Feature | Traditional DNS | DNS over HTTPS (DoH) |
|---------|----------------|----------------------|
| **Encryption** | ❌ Plain text | ✅ Encrypted (HTTPS) |
| **Port** | 53 (UDP/TCP) | 443 (same as websites) |
| **Visibility to ISP** | Fully visible | Looks like normal web traffic |
| **Blocking** | Easy to block/intercept | Hard to distinguish from regular browsing |


### Popular DoH Providers

| Provider | DoH URL |
|----------|---------|
| Cloudflare | `https://cloudflare-dns.com/dns-query` |
| Google | `https://dns.google/dns-query` |
| Quad9 | `https://dns.quad9.net/dns-query` |

Most modern browsers (Chrome, Firefox, Edge) support DoH in their settings.

---

## 🧩 Putting It All Together

Here's what happens when you type `google.com` with all these pieces in play:

```
You type google.com
      │
      ▼
┌─────────────────┐
│  Browser Cache   │ ── Hit? → Connect directly
│  (checked first) │
└────────┬────────┘
        │ Miss
        ▼
┌─────────────────┐
│    OS Cache      │ ── Hit? → Return to browser
└────────┬────────┘
        │ Miss
        ▼
┌─────────────────┐
│  VPN Tunnel?     │ ── Yes → DNS query goes through VPN
│  (if enabled)    │ ── No  → Goes to your ISP/router
└────────┬────────┘
        │
        ▼
┌─────────────────┐
│  DoH Enabled?    │ ── Yes → Encrypted DNS query (port 443)
│                  │ ── No  → Plain text DNS query (port 53)
└────────┬────────┘
        │
        ▼
┌─────────────────┐
│ Recursive        │ → Root Server → TLD Server → Authoritative Server
│ Resolver         │ → Returns: 142.250.190.46
└────────┬────────┘
        │
        ▼
  Browser connects
  to 142.250.190.46
  Google loads! 🎉

```
---


## 🌍 Geolocation & CDN Caching (How Websites Get Closer to You)

Imagine a pizza chain with only one kitchen in New York. If you order from India, your pizza would take forever to arrive!

**Solution?** Open kitchens in every major city. Now your pizza comes from the nearest one — hot and fast. 🍕

That's exactly what a **CDN (Content Delivery Network)** does for websites.

### How it works

Big websites like Google, Netflix, and YouTube don't live on just one server. They copy their content to servers all around the world called **edge servers** or **PoPs (Points of Presence)**.

```
You (Mumbai) → google.com
      │
      ▼
DNS uses your location (geolocation)
to pick the nearest server
      │
      ▼
Instead of a server in California (150ms+ away)
you get a server in Mumbai (5ms away)
      │
      ▼
Page loads instantly! ⚡
```

### How does DNS know your location?

| Method | How it works |
|--------|-------------|
| **Your IP address** | Every IP belongs to a region — DNS servers maintain databases mapping IPs to locations |
| **EDNS Client Subnet (ECS)** | Your resolver sends a partial IP to the authoritative server so it can return the closest server |
| **Anycast routing** | Multiple servers share the same IP — network routing automatically sends you to the nearest one |

### Geolocation caching layers

| Layer | What happens | Example |
|-------|-------------|---------|
| **CDN Edge Server** | Caches static content (images, CSS, JS) at the nearest PoP | CloudFront edge in Mumbai serves images instead of fetching from US |
| **Regional Cache** | A middle layer between edge and origin — reduces load on the main server | CloudFront Regional Edge Cache in India |
| **Origin Server** | The actual source — only hit when no cache has the content | Google's data center in the US |

### Real-world example

When you watch a YouTube video from India:

1. DNS resolves `youtube.com` → an IP of a **nearby CDN edge server** (not a US server)
2. The edge server checks: "Do I have this video cached?"
   - ✅ **Yes** → streams it directly to you (super fast)
   - ❌ **No** → fetches it from a regional cache or origin, caches it, then streams to you
3. The next person in your city who watches the same video gets it even faster

### Popular CDN providers

| Provider | Used by |
|----------|---------|
| **CloudFront** | Amazon, AWS customers |
| **Cloudflare** | ~20% of all websites |
| **Akamai** | Banks, media companies |
| **Google Global Cache** | YouTube, Google services |

### Why this matters

Without geolocation-aware DNS + CDN caching:
- A user in Tokyo would wait for data from a server in Virginia
- Every single request would hit the origin server (slow + expensive)
- Video streaming would buffer constantly

With it:
- Content is served from the nearest city
- Origin servers handle far less traffic
- Your experience feels instant, no matter where you are

---

## 🏗️ DNS in System Design: Interview-Ready Q&A

---

### Q1: You're designing a globally distributed system. How does DNS fit in?

DNS is the **first hop** in every request. In a global system, DNS does more than name resolution — it becomes a **traffic routing layer**.

**Key design decisions:**

- **Latency-based routing** — DNS resolves to the server closest to the user (e.g., Route 53 latency routing)
- **Failover routing** — If a region goes down, DNS automatically resolves to a healthy region
- **Weighted routing** — Gradually shift traffic during deployments (e.g., 90% old, 10% new)
- **Geolocation routing** — Serve region-specific content or comply with data residency laws


User (India) → DNS → Mumbai endpoint
User (US)    → DNS → Virginia endpoint
User (EU)    → DNS → Frankfurt endpoint

---

### Q2: How would you design a system like Route 53 or Cloudflare DNS?

**Core components:**
```

┌──────────────┐     ┌──────────────────┐     ┌────────────────┐
│  Authoritative│     │  Recursive        │     │  Health Check   │
│  Name Servers │◄────│  Resolvers        │     │  Service        │
│  (Anycast)    │     │  (Global cache)   │     │  (per endpoint) │
└──────┬───────┘     └──────────────────┘     └───────┬────────┘
      │                                               │
      └───────────── Routing Policy Engine ◄──────────┘
                     (latency/geo/weighted/failover)

```
**Design considerations:**

| Concern | Approach |
|---------|----------|
| **Availability** | Anycast — same IP advertised from multiple locations; traffic auto-routes to nearest healthy node |
| **Scalability** | Stateless authoritative servers + aggressive caching at every layer |
| **Consistency** | Eventual consistency is acceptable — TTL controls propagation delay |
| **Health checks** | Active probes to endpoints; unhealthy endpoints removed from DNS responses |
| **DDoS protection** | Rate limiting, anycast absorption, overprovisioned capacity |

---

### Q3: What's the impact of TTL on system design?

TTL is a **trade-off between freshness and performance**.

| TTL Value | Pros | Cons | Use when |
|-----------|------|------|----------|
| **Low (30–60s)** | Fast failover, quick traffic shifts | More DNS queries, higher resolver load | Active failover, blue-green deploys |
| **Medium (300s)** | Balanced | Moderate propagation delay | Most production services |
| **High (3600s+)** | Fewer lookups, lower cost | Slow failover, stale records linger | Stable, rarely-changing endpoints |

**System design tip:** During a migration or incident, you can't force clients to respect your TTL. Some resolvers and apps ignore it. Always design for the case where stale DNS records are being used.

---

### Q4: How do you handle DNS failover without downtime?

**Strategy: Active-Active with health checks**
```

                   ┌─── Health Check ───┐
                   │                    │
             ┌─────▼─────┐      ┌──────▼─────┐
User → DNS →  │ Region A   │      │ Region B    │
             │ (primary)  │      │ (secondary) │
             └────────────┘      └─────────────┘
```
Region A healthy  → DNS returns Region A IP
Region A down     → DNS returns Region B IP (within TTL window)

**Key points for interviews:**

- Health checks run every 10–30 seconds
- Failover time = health check interval + TTL propagation
- For near-instant failover → use low TTL + Anycast (not DNS failover)
- DNS failover is **not instant** — always pair with retries at the application layer

---

### Q5: DNS vs Load Balancer — when do you use which?

| Aspect | DNS-based routing | Load Balancer |
|--------|-------------------|---------------|
| **Layer** | Works at DNS resolution time (before connection) | Works at connection/request time (L4/L7) |
| **Granularity** | Per-domain, per-region | Per-request, per-connection |
| **Health checks** | Slow (TTL-dependent propagation) | Fast (real-time, per-request) |
| **Session stickiness** | Not possible | Supported |
| **Cost** | Cheap — no infrastructure in the data path | More expensive — sits in the request path |
| **Use case** | Global traffic distribution across regions | Distributing traffic across servers within a region |

**System design answer:** Use both.


User → DNS (Route 53: picks region)
    → Load Balancer (ALB: picks server within region)
    → Application Server

---

### Q6: What is DNS prefetching and how does it improve performance?

When a browser loads a page, it scans for links to other domains and **resolves their DNS in the background** before the user clicks.

html
<!-- Hint the browser to pre-resolve these domains -->
<link rel="dns-prefetch" href="//cdn.example.com">
<link rel="dns-prefetch" href="//api.example.com">

**System design relevance:**
- Reduces perceived latency by 50–300ms per external domain
- Critical for pages that load resources from multiple domains (CDN, analytics, ads)
- Browsers do this automatically for links on the page, but you can add hints for API domains

---

### Q7: How does DNS work in microservices / service discovery?

Inside a distributed system, services need to find each other. DNS is one approach:

| Approach | How it works | Example |
|----------|-------------|---------|
| **Internal DNS** | Each service registers a DNS name; others resolve it | `payment-service.internal.company.com` |
| **Service mesh / registry** | Services register with a central registry; clients query it | Consul, Eureka, AWS Cloud Map |
| **Kubernetes DNS** | Built-in DNS — every service gets a name automatically | `my-service.my-namespace.svc.cluster.local` |

**Trade-offs:**

- DNS is simple but has **TTL lag** — stale records during scaling events
- Service registries are real-time but add **operational complexity**
- In practice, most systems use **DNS for external traffic** and **service registries for internal traffic**

---

### Q8: A customer reports intermittent "site not reachable" errors. How do you debug DNS?

**Systematic approach:**

bash
 1. Check what DNS returns right now
dig google.com
nslookup google.com

2. Query a specific DNS server to isolate the problem
dig @8.8.8.8 google.com        # Google's DNS
dig @1.1.1.1 google.com        # Cloudflare's DNS

3. Trace the full resolution path
dig +trace google.com

4. Check TTL — is a stale record being served?
dig google.com | grep TTL

5. Check if it's a specific record type issue
dig AAAA google.com             # IPv6
dig CNAME www.google.com        # Alias

 6. Check from the client's perspective
 Is their /etc/resolv.conf pointing to the right resolver?
cat /etc/resolv.conf

**Common root causes in system design:**

| Symptom | Likely cause |
|---------|-------------|
| Works for some users, not others | DNS propagation delay (TTL not expired everywhere) |
| Fails intermittently | DNS server returning mixed healthy/unhealthy IPs (stale health check) |
| Works on one device, not another | Local DNS cache or different resolver configured |
| Fails after a deployment | DNS record updated but old TTL hasn't expired |
| Timeout errors | DNS server unreachable or rate-limited |

---

### Q9: How would you estimate DNS lookup overhead in a system design calculation?

**Rough numbers to use:**

| Scenario | Latency |
|----------|---------|
| Browser cache hit | ~0ms |
| OS cache hit | ~1ms |
| Local resolver (same network) | ~1–5ms |
| ISP resolver (same city) | ~5–20ms |
| Recursive resolution (cold, full chain) | ~50–200ms |
| Cross-continent resolution | ~100–300ms |

**Rule of thumb for back-of-envelope:**
- Assume **~5ms** for cached/warm DNS
- Assume **~100ms** for cold DNS
- For a page loading 20 external domains → cold DNS adds **~2 seconds** without prefetching

---


## ❓ Quick FAQ

**Q: Can I change my DNS server?**
Yes! You can use Cloudflare (`1.1.1.1`), Google (`8.8.8.8`), or others instead of your ISP's default. This can improve speed and privacy.

**Q: Does a VPN slow down my internet?**
Usually a little, because your traffic takes an extra hop through the VPN server. The closer the server, the less the slowdown.

**Q: Is DoH the same as a VPN?**
No. DoH only encrypts your *DNS lookups*. A VPN encrypts *all* your internet traffic. They solve different problems and can be used together.

**Q: What's a DNS leak?**
When your DNS queries bypass the VPN and go directly to your ISP, exposing which sites you visit despite being "protected" by a VPN.

---
