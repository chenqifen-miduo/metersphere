#!/usr/bin/env pwsh
# Miduo SSO 冒烟（灰度）
param(
  [string]$Base = "https://v3-x-metersphere.miduo.org"
)

$ErrorActionPreference = "Stop"
Write-Host "GET $Base/front/auth/miduo/status"
try {
  $r = Invoke-RestMethod -Uri "$Base/front/auth/miduo/status" -Method GET
  $r | ConvertTo-Json -Depth 5
} catch {
  Write-Host "status failed: $_"
  exit 1
}
Write-Host "OK"
