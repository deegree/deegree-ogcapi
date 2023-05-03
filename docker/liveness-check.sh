#!/bin/bash

set -e

if [ "$DEEGREE_CONTEXT_PATH" = "ROOT" ]; then
  CONTEXT=""
else
  CONTEXT=$(echo "$DEEGREE_CONTEXT_PATH" | tr "#" "/")
  CONTEXT="/$CONTEXT"
fi

echo "Determined context $CONTEXT"

URL="http://localhost:8080${CONTEXT}/datasets"
echo "Checking URL ${URL}..."

curl --fail "$URL" || exit 1
