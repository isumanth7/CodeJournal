# Kafka Long-Term Learning Notes

## Week 0: Distributed Systems Prerequisites

### What is a Distributed System and Why We Need Them

A **distributed system** is a collection of independent computers that appear to users as a single coherent system. They communicate over a network to coordinate work.

#### Why we need them

**1. Scale beyond one machine**
A single server has limits — CPU, RAM, disk, network bandwidth. When your app serves millions of users or stores petabytes of data, one machine simply can't handle it.

**2. Fault tolerance**
If you run on one machine and it dies, everything is down. Distributed systems spread work across multiple machines so the system survives individual failures.

**3. Low latency globally**
Users in India shouldn't wait for a server in the US to respond. Distributed systems place data/compute closer to users.

**4. Throughput**
More machines = more parallel processing = more requests handled per second.

#### Real-world analogy

- **Single system** = one chef doing everything (cooking, plating, serving). Works for 5 customers, breaks at 500.
- **Distributed system** = multiple chefs, each handling different dishes, coordinated by a head chef. Scales to thousands of orders. If one chef is sick, others cover.

#### The tradeoffs (why it's hard)

| Challenge | What it means |
|-----------|---------------|
| Network failures | Machines can't always talk to each other |
| Partial failures | Some nodes fail while others keep running |
| No global clock | You can't easily determine "what happened first" |
| Consistency | Keeping data the same across all copies is expensive |
| Coordination | Getting machines to agree on something takes time |

#### How this connects to Kafka

Kafka is a distributed system itself:
- **Multiple brokers** → scale and fault tolerance
- **Partitions** → spread data across machines for throughput
- **Replication** → survive broker failures
- **Leader election** → coordinate who handles writes

---

### CAP Theorem

*(Coming next)*

---

### Eventual Consistency vs Strong Consistency

*(Coming next)*

---

### Latency vs Throughput Tradeoffs

*(Coming next)*

---

### Horizontal vs Vertical Scaling

*(Coming next)*

---

### Replication & Partitioning

*(Coming next)*

---

### Messaging & Communication

*(Coming next)*

---

### Consensus & Coordination

*(Coming next)*

---

## Week 1: Kafka Core Fundamentals

*(Coming next)*

---

## Week 2: Internals & Performance

*(Coming next)*

---

## Week 3: Advanced & Interview Prep

*(Coming next)*

---
