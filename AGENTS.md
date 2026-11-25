# Agent instructions

## Repository rules

| Rule | Enforced by |
|---|---|
| Campaign redemption limits must be positive. | `@Min(1)` and the `per_user_limit > 0` database check in `schema.sql`; tested in `VoucherServiceTest`. |
| Spring persistence tests must not leak redeemed vouchers or campaign changes into another test. | `@Transactional` on `VoucherServiceTest`; enforced in CI by `./gradlew test`. |
| Redemption-count enforcement must remain in the transaction that owns the campaign lock. | `@Transactional redeem(...)` and `CampaignRepository.findWithLockById(...)`; CI tests cover seeded and custom limits. |
| The redemption lookup used for per-user limits must have a campaign/user index. | `idx_redemption_campaign_user` in `schema.sql`; CI starts the schema from this file. |
