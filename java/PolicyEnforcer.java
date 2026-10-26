import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PolicyEnforcer {

    // Requirement 01: Policy configuration containing at least 3 rule types (spending limit, allowlist, time window)
    public static class PolicyConfig {
        double maxSpendingLimitUSD;
        List<String> allowlistedAddresses;
        LocalTime windowStart;
        LocalTime windowEnd;

        public PolicyConfig(double limit, List<String> allowlist, LocalTime start, LocalTime end) {
            this.maxSpendingLimitUSD = limit;
            this.allowlistedAddresses = allowlist;
            this.windowStart = start;
            this.windowEnd = end;
        }
    }

    public static class ActionRequest {
        String recipient;
        double amountUSD;
        LocalDateTime requestTime;

        public ActionRequest(String recipient, double amountUSD, LocalDateTime requestTime) {
            this.recipient = recipient;
            this.amountUSD = amountUSD;
            this.requestTime = requestTime;
        }
    }

    // Requirement 04: Audit log entry container
    public static class AuditLogEntry {
        LocalDateTime timestamp;
        String recipient;
        double amount;
        boolean allowed;
        String decisionReason;

        public AuditLogEntry(String recipient, double amount, boolean allowed, String decisionReason) {
            this.timestamp = LocalDateTime.now();
            this.recipient = recipient;
            this.amount = amount;
            this.allowed = allowed;
            this.decisionReason = decisionReason;
        }

        @Override
        public String toString() {
            return String.format("[%s] Target: %s ($%.2f) -> %s | Reason: %s",
                    timestamp, recipient, amount, allowed ? "ALLOWED" : "DENIED", decisionReason);
        }
    }

    private final PolicyConfig policy;
    private final List<AuditLogEntry> auditTrail = new ArrayList<>();

    public PolicyEnforcer(PolicyConfig policy) {
        this.policy = policy;
    }

    // Requirements 02 & 03: Evaluates rules, returns specific decision reason, and prevents execution if denied
    public boolean evaluateAction(ActionRequest request) {
        // Rule Type 1: Spending Limit Check
        if (request.amountUSD > policy.maxSpendingLimitUSD) {
            String reason = "Denied by Spending Limit Rule: Amount $" + request.amountUSD + " exceeds max limit of $" + policy.maxSpendingLimitUSD;
            auditTrail.add(new AuditLogEntry(request.recipient, request.amountUSD, false, reason));
            System.out.println("❌ " + reason);
            return false;
        }

        // Rule Type 2: Allowlist Check
        if (!policy.allowlistedAddresses.contains(request.recipient)) {
            String reason = "Denied by Allowlist Rule: Recipient " + request.recipient + " is not in the approved allowlist.";
            auditTrail.add(new AuditLogEntry(request.recipient, request.amountUSD, false, reason));
            System.out.println("❌ " + reason);
            return false;
        }

        // Rule Type 3: Time Window Check
        LocalTime time = request.requestTime.toLocalTime();
        if (time.isBefore(policy.windowStart) || time.isAfter(policy.windowEnd)) {
            String reason = "Denied by Time Window Rule: Request time " + time + " is outside allowed window (" + policy.windowStart + " - " + policy.windowEnd + ").";
            auditTrail.add(new AuditLogEntry(request.recipient, request.amountUSD, false, reason));
            System.out.println("❌ " + reason);
            return false;
        }

        // All rules satisfied
        String reason = "Allowed: All policy rules successfully satisfied.";
        auditTrail.add(new AuditLogEntry(request.recipient, request.amountUSD, true, reason));
        System.out.println("✅ " + reason);
        
        // Executes action only if permitted (Requirement 03)
        executeAction(request);
        return true;
    }

    private void executeAction(ActionRequest request) {
        System.out.println("🚀 [EXECUTION] Action safely processed on-chain for recipient: " + request.recipient);
    }

    public void printAuditTrail() {
        System.out.println("\n=== AUDIT TRAIL LOG ===");
        for (AuditLogEntry entry : auditTrail) {
            System.out.println(entry);
        }
    }

    // Requirement 03 & 04: Demonstration covering success and denials for each rule type
    public static void main(String[] args) {
        List<String> allowlist = new ArrayList<>();
        allowlist.add("0xTrustedProtocol123");
        allowlist.add("0xApprovedVault456");

        PolicyConfig config = new PolicyConfig(
            500.0, // Spending limit
            allowlist, 
            LocalTime.of(8, 0), // Window start
            LocalTime.of(18, 0) // Window end
        );

        PolicyEnforcer enforcer = new PolicyEnforcer(config);

        System.out.println("=== STARTING POLICY GATEKEEPER AGENT ===");

        LocalDateTime validTime = LocalDateTime.of(2026, 10, 26, 12, 0);

        // 1. Valid Request (Passes all rules)
        System.out.println("\nTest 1: Valid Request");
        enforcer.evaluateAction(new ActionRequest("0xTrustedProtocol123", 200.0, validTime));

        // 2. Denial by Spending Limit
        System.out.println("\nTest 2: Spending Limit Denial");
        enforcer.evaluateAction(new ActionRequest("0xTrustedProtocol123", 800.0, validTime));

        // 3. Denial by Allowlist
        System.out.println("\nTest 3: Allowlist Denial");
        enforcer.evaluateAction(new ActionRequest("0xUnauthorizedContract999", 100.0, validTime));

        // 4. Denial by Time Window
        LocalDateTime nightTime = LocalDateTime.of(2026, 10, 26, 22, 0);
        System.out.println("\nTest 4: Time Window Denial");
        enforcer.evaluateAction(new ActionRequest("0xTrustedProtocol123", 100.0, nightTime));

        // Display full audit log (Requirement 04)
        enforcer.printAuditTrail();
    }
}
