# Submission Guide — GitHub Repo + Word Answer Sheet

The coursework checklist asks for three things. Here's exactly how to
produce each one from this project.

## 1. Java source files + the .accdb database, as a GitHub repo link

```bash
cd firstbank-account-app
git init
git add .
git commit -m "First Bank Uganda account opening app - OOP coursework"
```

Create an empty repo on GitHub (e.g. `firstbank-account-app`), then:

```bash
git remote add origin https://github.com/<your-username>/firstbank-account-app.git
git branch -M main
git push -u origin main
```

**Important:** run the app at least once locally (`mvn javafx:run`) and
submit a few test accounts through the form *before* you commit, so that
`data/firstbank.accdb` actually exists and contains sample records. Then
remove `data/` from `.gitignore` (or `git add -f data/firstbank.accdb`)
so the populated database file is included in the repo, since the brief
asks for the database file itself, not just the schema.

Put the resulting GitHub URL into your Word answer sheet.

## 2. Screenshots embedded in the Word answer sheet

Run the application yourself (`mvn javafx:run`) and capture these moments
— each one demonstrates a specific marked requirement:

1. **Empty form on launch** — shows the full layout (all fields, the
   three DOB combo boxes, Account Type, Branch, buttons).
2. **Day combo box auto-adjusting for February in a leap year** — pick
   Year = 2024, Month = February, and screenshot the Day list open
   showing it goes up to 29.
3. **Day combo box for February in a non-leap year** — same but Year =
   2023, showing the Day list stopping at 28.
4. **Validation in action** — submit the form with deliberate mistakes
   (e.g. a lowercase NIN, mismatched emails, a PIN of `0000`) and
   screenshot the inline red error messages plus the summary error
   dialog.
5. **Successful submission** — a fully valid Savings (or any type)
   account, showing the populated "Account Summary is Below:" text area
   with the formatted record and generated account number.
6. **Joint account second-NIN behaviour** — Account Type = Joint with the
   Second NIN field enabled and required.
7. **Student account age rule** — attempt a Student account with an
   applicant outside 18–25 and screenshot the resulting age error.
8. *(Optional but a nice touch)* — open `firstbank.accdb` in Microsoft
   Access (or any Access viewer) showing the `Accounts` table with the
   rows your test submissions created.

For each screenshot, add 1–2 sentences underneath explaining which
requirement it demonstrates — markers mainly check that your evidence
maps to the marking scheme.

## 3. Documentation: setup, compile, operate

You already have this — it's `README.md` in the project root. You can
either:
- paste its contents into the Word answer sheet as a "Setup &
  Operation" section, or
- simply state in the Word answer sheet "see README.md in the GitHub
  repository" and link to it directly (`https://github.com/<you>/firstbank-account-app/blob/main/README.md`).

## Suggested Word document structure

1. Cover page — module, group members + student numbers, date
2. Question 1 — brief design overview (Account hierarchy diagram or
   class list, validation approach, DB choice and why UCanAccess was
   used instead of the JDBC-ODBC bridge)
3. Screenshots (the 6–8 above, each captioned)
4. Setup/compile/operate instructions (from README.md)
5. GitHub repository link
6. Any known limitations / extensions attempted

## A note for group work

Since this is a 5-student group coursework, it's worth dividing the
demonstration work even though the codebase itself is one cohesive
project:
- One person owns the GitHub repo and final push
- One or two people run the app and capture the screenshots above
- One person drafts the Word document structure and design write-up
- Everyone reviews the final submission together before the deadline
