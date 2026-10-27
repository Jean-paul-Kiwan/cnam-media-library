# CNAM media library

**Source archived; run not verified.** Owner-reported CNAM university coursework (NFP121 context in source). This repository documents a project, not completion of a degree.

Java/Swing educational media library with console UI, media CRUD, filters, usage-report strategies, XML/binary persistence and CSV/XML export. EmailNotifier prints simulated notifications; it does not send email.

Original student data, reports, exports and compiled output are excluded. The included XML uses invented student accounts and deliberately non-secret demo passwords. This teaching authentication/persistence design is not production security.

Candidate PowerShell setup, **not run on this snapshot**: install/provide a suitable JDK, then `javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)` and `java -cp out app.Main`. Exact JDK/build success not verified. No screenshots, deployment or test result claimed.
