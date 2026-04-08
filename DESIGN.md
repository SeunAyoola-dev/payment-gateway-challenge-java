# ADR: Payment Gateway Design & Implementation

## Status
Submitted

## Context
The requirement was to build a Payment Gateway API that processes card payments via a simulated bank and allows for retrieval of payment details. Key constraints included PCI-DSS compliance (data masking), input validation (Fail-Fast), and handling external service unavailability.

## Decision
I have implemented a **Layered Architecture** (Controller-Service-Repository) using Spring Boot, with the following specific design choices:

### 1. Decoupled Validation Strategy
**Decision:** I implemented a two-tiered validation approach using both Jakarta Bean Validation (`@Valid`) and a dedicated `PaymentRequestValidator` service.
**Consequence:** While adding a layer of redundancy, this ensures that the core business logic remains testable even when the framework's bean validation is bypassed in unit tests. It centralizes complex rules (like expiry logic) into a readable, easily amendable component.

### 2. Business Logic Decoupling
**Decision:** High separation of concerns between internal business models and external Bank API DTOs using dedicated Mappers.
**Consequence:** This follows the industry standard for scalability. The internal logic is fully shielded from changes in the Bank Simulator's API, allowing us to swap providers or upgrade the bank interface with zero impact on the core Gateway service.

### 3. "Privacy by Design" (Compliance)
**Decision:** I adopted a "Never Store" policy for sensitive PII. Full card numbers and CVVs are used only for the transit to the bank and are immediately discarded. Only the last four digits are persisted.
**Consequence:** This minimizes the gateway's PCI-DSS footprint. While a production system would require a robust encryption/tokenization service, this approach ensures that a breach of the Gateway's storage would yield no sensitive card data.

### 4. Semantic Error Handling (Retriability)
**Decision:** External communication failures result in a `503 Service Unavailable` instead of a generic `500 Internal Server Error`.
**Consequence:** This provides clear signaling to the Merchant's automated systems. A `503` indicates a transient dependency issue, allowing the merchant to implement intelligent retry logic or failover strategies rather than interpreting it as a terminal application crash (`500`).

### 5. Selective Persistence
**Decision:** Only payments with a status of `Authorized` or `Declined` are stored in the repository. `Rejected` payments (validation failures) are not persisted.
**Consequence:** This aligns strictly with the retrieval requirements, which only specify the retrieval of previously processed payments. It keeps the storage layer optimized for successful/declined transactions and avoids bloating the database with malformed requests.

## Consequences
*   **Pros:** Highly testable, PCI-compliant storage, clean separation of concerns, and merchant-friendly error signaling.
*   **Cons:** In-memory storage is volatile.
