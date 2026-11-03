#!/bin/sh
set -eu
# This secret belongs to the CDS branch, not to the disposable container layer.
# Refuse to start rather than generate a different encryption identity on rebuild.
: "${MS_WECOM_SECRET_MASTER_KEY:?Configure a stable branch-only runtime secret in CDS}"
# Override the legacy source-build path only inside this dedicated preview image.
export MS_CONFIG_DIR=/opt/metersphere/conf
export MS_REDISSON_CONFIG=file:/opt/metersphere/conf/redisson.yml
exec /deployments/run-with-wecom-bridge.sh
