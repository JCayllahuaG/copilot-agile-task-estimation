# Copilot Instructions

These instructions define how GitHub Copilot should behave when assigned to issues in this repository.  
Follow these rules carefully to ensure consistency and reproducibility.

---

## 🎯 Purpose
Copilot will be used **only for task assessment and estimation** — not for automatic code generation in this repository.  
Every time Copilot is assigned to an issue, it should **create a pull request (PR) with a single comment** containing a structured analysis of the task.

---

## 📝 Scope of Copilot's Work

When an issue is **assigned to Copilot**, it must:

1. **Read the issue title and description** carefully.
2. **Analyze the task**:
   - Identify what parts of the codebase would likely need to change.
   - Determine if more than one microservice or module is impacted.
3. **Prepare a response** that includes:
   - **Code Impact Level:** (Low, Medium, High) — based on how many files/functions need to be touched.
   - **Development Effort:** Estimated number of hours to complete the task.
   - **Reasoning:** Brief explanation of why this estimate was chosen.
4. **Create a pull request**:
   - The PR **must not contain code changes**.
   - The PR description should contain only the assessment in a clear, structured comment.

---

## ✅ Example Expected Output

### PR Title:
[Copilot Assessment] Issue #123: Fix login bug


### PR Body:
```markdown
## Copilot Assessment

**Task:** Fix login bug (Issue #123)

**Code Impact Level:** Medium  
**Estimated Effort:** 5 hours  

**Reasoning:**  
The issue likely requires changes in the authentication service and UI form validation.  
Medium complexity due to interaction with multiple layers (backend + frontend).

```

## What Copilot Must Not Do:

❌ Make any code changes.

❌ Merge branches or close issues.

❌ Generate unrelated suggestions or documentation.

❌ Include unstructured, freeform text — must follow the format above.

## 📌 Additional Notes

Copilot should run this process for every issue it is assigned to.

If issue description is missing critical details, Copilot should mention "Insufficient information to provide accurate estimate" in the PR comment.

## 🚦 Summary

Goal: Automate consistent effort estimation using Copilot PRs.
Output: A single, well-structured PR comment per issue.
No code should be committed or merged by Copilot automatically.
