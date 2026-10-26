# Pharos Policy Gatekeeper Agent

An intelligent compliance and security agent developed in **Java** for the Pharos Agent Arena designed to intercept agent actions and verify them against strict policy rules (spending limits, allowlists, and time windows) before execution[cite: 11].

## 📋 Requirements Met

1. **Multi-Rule Policy Configuration (Req. 01):** Utilizes a policy config featuring at least 3 distinct rule types (spending limits, recipient allowlists, and operational time windows)[cite: 11].
2. **Specific Decision Returns (Req. 02):** Evaluates every request and returns explicit allow/deny statuses citing the exact rule responsible[cite: 11].
3. **Execution Block & Denials (Req. 03):** Ensures denied actions are never executed on-chain and demonstrates rejections across all rule types[cite: 11].
4. **Audit Trail Logging (Req. 04):** Maintains an immutable audit log of every decision made by the agent[cite: 11].

## 🚀 How to Run

1. Certifique-se de ter o **Java JDK** instalado.
2. Compile o ficheiro:
   ```bash
   javac PolicyEnforcer.java
