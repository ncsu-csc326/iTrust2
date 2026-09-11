---
name: uc-audit-SM
description: Samreen's daily requirements-traceability audit for iTrust2. Picks the next un-audited Use Case (UC1-UC14) from docs/uc-audit-progress.md, maps its requirements to code, flags implementation gaps, maps requirements to existing test cases (automated + manual), flags anomalies, and writes a dated report. Invoke with /uc-audit-SM, or /uc-audit-SM UC5 to force a specific UC.
---

# UC Audit Skill

You are running the daily requirements-traceability audit for the iTrust2 project. This
reproduces, as a repeatable procedure, the manual analysis already done for UC1-UC3
(see docs/uc-audit-progress.md, docs/test-cases-UC1-UC3.md, and
docs/manual-test-cases-UC1-UC2.md for the reference examples of the expected depth and
format).

Do this for exactly **one** Use Case per invocation, end to end. Do not batch multiple
UCs in one run unless the user explicitly asks.

## Step 0: Pick the target UC

- Read `docs/uc-audit-progress.md`. It lists UC1-UC14 with a status (`done`, `in
  progress`, `not started`) and the date last audited.
- If the user passed an argument naming a specific UC (e.g. `/uc-audit UC5`), audit
  that one regardless of status.
- Otherwise, pick the first UC still marked `not started`. If all 14 are `done`, tell
  the user the full cycle is complete and ask whether to re-audit UC1 (catching drift
  since the last pass) or stop.
- Read `docs/UC<N>.md` in full before doing anything else — every sub-flow, alternative
  flow, logging table entry, and data-format row in it is a candidate requirement row.

## Step 1: Map requirements -> code

For every distinct requirement in the doc (main flow, each [S#] sub-flow, each [E#]
alternative flow, each row of the Logging table, each row of the Data Format table if
present), find the actual implementing code:

- Controllers: `iTrust2/src/main/java/edu/ncsu/csc/itrust2/controllers/api/`
- Models/forms: `iTrust2/src/main/java/edu/ncsu/csc/itrust2/models/`,
  `.../forms/`
- Services: `iTrust2/src/main/java/edu/ncsu/csc/itrust2/services/`
- Frontend (Thymeleaf/AngularJS): `iTrust2/src/main/resources/templates/`,
  `iTrust2/src/main/resources/static/js/`
- Transaction/logging codes: `models/enums/TransactionType.java`

Read the actual method bodies — do not infer correctness from a plausibly-named class
existing. Specifically check:
- Does validation exist, and does it match the stated data format (lengths, character
  sets, required-ness)?
- Do transaction codes match the numeric ranges the doc's own Logging table specifies?
- Does the primary/secondary MID attribution in a log call match what the doc's table
  says (this exact class of bug was found in UC1's code-101 entry — check for it
  elsewhere too)?
- Are all documented roles/actors (Patient, HCP, Optometrist, Ophthalmologist, ER, Lab
  Tech, Admin) actually handled, not just the obvious one or two?

Assign each requirement row a **% mapped** using this rubric:
- **100%**: fully implemented, matches the doc's stated behavior and data formats.
- **70-95%**: implemented but with a partial gap or a doc/code deviation (specify it).
- **1-50%**: only partially present (e.g. client-side check exists but no server-side
  enforcement, or one sub-case works and another doesn't).
- **0%**: no corresponding code found at all after a real search — not just "didn't
  look hard enough." Grep for plausible alternate names before concluding 0%.

Flag every deviation explicitly, even small ones (off-by-one transaction codes, wrong
MID as primary vs secondary, missing confirmation dialogs, client-only validation with
no server-side backstop). These are exactly the kind of finding that made the UC1-UC3
pass valuable — don't smooth them over.

## Step 2: Map requirements -> test cases

For the same set of requirement rows:
- Search `iTrust2/src/test/java/` for existing automated tests that exercise this
  requirement (not just this controller/class in general — the *specific* requirement,
  e.g. a lockout threshold, a specific validation rule, a specific logging code).
- Search `docs/manual-test-cases-*.md` and `docs/test-cases-*.md` for existing manual
  or API-style test cases covering it.
- For every requirement with **no** existing test of either kind, write one manual UI
  test case in the style of `docs/manual-test-cases-UC1-UC2.md` (real field labels and
  on-screen messages pulled from the actual Thymeleaf templates — do not invent labels;
  read the template) and note that it is new. Do not silently skip untested
  requirements just because they're inconvenient to test manually.
- For requirements where the underlying feature itself is a 0% code gap (Step 1), still
  write the manual test case as a "BLOCKED — feature gap" case (see the UC1-06/UC2-08
  pattern in the existing docs) rather than omitting it.

## Step 3: Flag anomalies

Anomalies are anything that doesn't fit cleanly into "implemented" or "not
implemented" — surface these explicitly in their own section of the report:
- Doc says X, code does Y (a real behavioral deviation, not just a gap).
- A requirement that only exists in the UI, or only in the API, but not both.
- Inconsistent enforcement between similar mechanisms (e.g. the UC2 finding that
  IP-level lockout is enforced by a servlet filter but user-level lockout is not
  enforced at authentication time at all, relying only on counting failed attempts).
- Any transaction/logging code numbering that doesn't match the doc's own Logging
  table, even if the functionality otherwise works.
- Anything a previous UC audit's findings might affect (e.g. shared infrastructure like
  `LoggerUtil`, `WebSecurityConfig`, or a shared model like `User`/`Patient` — check
  `docs/uc-audit-progress.md`'s "Cross-cutting notes" section for what prior passes
  already found, and don't re-report the exact same finding as new unless it's newly
  relevant to this UC too, in which case cross-reference it).

## Step 4: Write the report

Produce a single dated report at `docs/uc-audit/UC<N>-audit-<YYYY-MM-DD>.md`
containing:
1. A requirement -> code -> test case table (matching the exact column format used in
   the UC1-UC3 consolidated table shown earlier in this project's history: #, UC,
   Requirement, Code, Manual/Automated Test Case, % Mapped, Flag).
2. An overall average % for the UC.
3. A "Gaps requiring a decision/defect ticket" table (Gap, Severity, Where).
4. An "Anomalies" section (Step 3 findings), each with a one-line recommendation
   (log as defect / confirm intentional / needs its own follow-up audit).
5. Any new manual test cases written in Step 2, in full (ready to append to the
   relevant `docs/manual-test-cases-*.md` file — ask the user before actually editing
   that shared file, since it's a append to a document other UCs also use).

Send the report to the user with SendUserFile.

## Step 5: Update the tracker

Update `docs/uc-audit-progress.md`:
- Mark this UC `done`, with today's date and the overall % from Step 4.
- Add a one-line note to the "Cross-cutting notes" section if this pass found anything
  that future UC audits should know about (shared code, a systemic pattern, etc.).
- Do not mark it done if the user interrupted the audit partway through — leave it
  `in progress` with a note on how far it got.

## Notes on running this daily

- Each invocation should be self-contained — do not assume you remember findings from
  a previous day's session. `docs/uc-audit-progress.md` and the prior dated reports
  under `docs/uc-audit/` are the persistent memory for this workflow, not the
  conversation.
- If the user wants this fully automated (no manual daily invocation), point them at
  the `schedule` skill to set up a recurring cloud agent that runs `/uc-audit-SM`
  once a day — but do not set that up unless they ask for it.
