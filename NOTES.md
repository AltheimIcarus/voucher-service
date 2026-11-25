# Notes

## 2.1 What changed and why

- Added `CampaignDefaults` with `PER_USER_LIMIT` constant for the `perUserLimit` default in `Campaign`.
- Kept the `perUserLimit` in the campaign record and enforced it from the redemption count inside the existing _pessimistic_ campaign lock.

---

## 2.2 Deliberately not changed

- The sql seed data remains unchanged so that the schema change (+1 col) does minimal impact to existing schema.
- Voucher and Redemption schema untouched. Existing structures are sufficient to support the enforcement of `perUserLimit` policy. Hence no further derivation is required unless it optimize the performance and scalability.

---

## 2.3 Reflections

- **What did you get wrong first, and how did you notice?** I initially considered the `2` per-user limit as safe only as a hardcoded model default. I noticed that it would be duplicated across Java and database/configuration concerns, so I consolidated the Java default in a dedicated config package.
- **Which AI suggestion did you reject, and why?**
  > rejected deriving a user's redeemed count directly from the voucher table's `redeemed_by` col.
  > This makes the voucher table the enforcer for campaign policy
  > Thus require a full/expanded table lookup per campaign and user.
  > Poor performance and bad scalability without proper unique/index strategy
  > `Redemption` table is a clearer history source.
- **What took you longest?**
  > Setting up the Java 17 environment, github repo branches, and spinning the local gradle build + tests
