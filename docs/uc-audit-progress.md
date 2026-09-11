# iTrust2 UC Audit — Progress Tracker

Used by the `/uc-audit` skill to pick the next Use Case to review each day. Do not
reorder rows; the skill scans top-to-bottom for the first `not started` row unless the
user names a specific UC.

| UC | Title | Status | Last Audited | Overall % | Report |
|----|-------|--------|---------------|-----------|--------|
| UC1 | User Functionality | done | 2026-09-11 | 83% | docs/test-cases-UC1-UC3.md, docs/manual-test-cases-UC1-UC2.md |
| UC2 | Authenticate Users | done | 2026-09-11 | 75% | docs/test-cases-UC1-UC3.md, docs/manual-test-cases-UC1-UC2.md |
| UC3 | Log Transactions | done | 2026-09-11 | 98% | docs/test-cases-UC1-UC3.md |
| UC4 | Demographics | not started | — | — | — |
| UC5 | Hospitals | not started | — | — | — |
| UC6 | Appointments | not started | — | — | — |
| UC7 | Office Visit | not started | — | — | — |
| UC8 | Basic Health Metrics | not started | — | — | — |
| UC9 | Prescriptions | not started | — | — | — |
| UC10 | Diagnoses | not started | — | — | — |
| UC11 | Password Functionality | not started | — | — | — |
| UC12 | HCP Edit Demographics | not started | — | — | — |
| UC13 | View Access Logs | not started | — | — | — |
| UC14 | Alert Users by Email | not started | — | — | — |

## Cross-cutting notes

Findings here apply beyond a single UC — later audits should check whether they recur.

- **Logging MID attribution bug (found in UC1)**: `APIUserController.getUser()` logs
  the *viewed* user as the primary MID for transaction code 101, not the admin
  performing the view, contradicting the doc's own Logging table. Check other
  controllers' "view single record" endpoints for the same pattern.
- **Lockout/ban enforcement asymmetry (found in UC2)**: IP-level lockout/ban is
  enforced by a servlet filter (`IPFilter`) that blocks every subsequent request
  regardless of credentials. User-level lockout/ban has no equivalent enforcement at
  authentication time — `WebSecurityConfig`'s JDBC auth only checks the `enabled`
  column, so a correct password entered during a user's lockout window is not
  provably blocked by any code found so far. Not yet proven with a dedicated test.
- **No server-side length/format validation on `UserForm`/`User`** (found in UC1): only
  a client-side JS check (`min 6 chars` on password) exists in
  `admin/users.html`; nothing enforces the documented 6-20 char range end-to-end, and
  no username format check exists at all, client or server. Worth checking whether
  other forms (`PatientForm`, `HospitalForm`) have the same client-only-validation
  pattern, since those did have real `@Length`/`@Pattern` bean validation server-side
  when checked for the requirements-mapping pass (UC4/UC5 should confirm this holds).
- **No confirmation dialog before destructive actions** (found in UC1, user delete):
  check whether other "delete" flows (hospitals, appointments, etc.) have the same gap.
- **`docs/Requirements.md` UC5 Logging table numbering doesn't match code**: doc says
  501-504, code implements 500-503 for Hospital CRUD. Functionally fine, just a
  doc/code numbering drift — worth a quick check whether other UCs have similar
  off-by-one code/doc numbering drift.
