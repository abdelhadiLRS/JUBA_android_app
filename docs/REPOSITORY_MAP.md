# JUBA Repository Map

Updated: 2026-08-27

## Android platform

| Repository | Current state | Role |
|---|---|---|
| `abdelhadiLRS/JUBA_android_app` | Active | UserApp + StoreApp + ProviderApp |
| `abdelhadiLRS/JUBA` | Exists but currently empty | Main JUBA repository |
| `abdelhadiLRS/JUBA_LISAN` | Active | JUBA language project |
| `abdelhadiLRS/JUBA_Warehouse` | Active | Warehouse project |

## Important recovery note

The GitHub account currently exposes the repositories listed above. `JUBA_android_app` is the active Android source repository and contains the three Android applications.

`JUBA` exists on GitHub but currently has repository size `0`, so it cannot be treated as a recoverable source repository from GitHub alone. No destructive replacement is performed automatically.

## Android source preservation policy

- Source code stays in Git.
- `build/` and `.gradle/` generated data are excluded.
- Production APKs are preserved only when explicitly tracked under `app/prod/release/`.
- Local signing keys and credentials must never be committed.
- Each Android app remains independently buildable.

## Recovery workflow

When a previously used JUBA repository reappears or its source becomes available, compare it against this map before merging. Prefer preserving history and importing missing source rather than replacing an existing repository blindly.
