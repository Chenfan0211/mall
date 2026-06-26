# Apifox API Sync

This project syncs API docs to Apifox through OpenAPI 3.0.3.

## Files

- `docs/api/openapi/mall-openapi.json`: generated OpenAPI document.
- `tools/apifox/generate-openapi.ps1`: generates OpenAPI from Spring Controller source files.
- `tools/apifox/test-openapi-generator.ps1`: verifies the generated document.
- `tools/apifox/sync-apifox.ps1`: imports the document into Apifox.

## Local Commands

Generate:

```powershell
powershell -ExecutionPolicy Bypass -File tools/apifox/generate-openapi.ps1
```

Verify:

```powershell
powershell -ExecutionPolicy Bypass -File tools/apifox/test-openapi-generator.ps1
```

Sync:

```powershell
$env:APIFOX_PROJECT_ID="your_project_id"
$env:APIFOX_API_TOKEN="your_api_token"
powershell -ExecutionPolicy Bypass -File tools/apifox/sync-apifox.ps1 -Generate
```

If a full import times out, sync by tag:

```powershell
powershell -ExecutionPolicy Bypass -File tools/apifox/sync-apifox.ps1 -Generate -SplitByTag
```

If Apifox should exactly match the generated OpenAPI document, add the explicit cleanup flag:

```powershell
powershell -ExecutionPolicy Bypass -File tools/apifox/sync-apifox.ps1 -Generate -DeleteUnmatchedResources
```

## Rules

- Do not commit Apifox tokens.
- The sync uses `OVERWRITE_EXISTING` for matched APIs and schemas.
- The sync does not delete unmatched Apifox resources.
- Every generated operation includes success and common failure examples.
- Temporary request bodies are written as UTF-8 without BOM because Apifox rejects BOM-prefixed JSON bodies.
