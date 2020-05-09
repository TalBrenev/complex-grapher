#!/bin/bash

# Fail fast on error
set -e

echo "Transferring to S3..."

aws s3 sync ./build "s3://${AWS_S3_BUCKET_NAME}" --delete

echo "Invalidating CloudFront cache..."

aws cloudfront create-invalidation --distribution-id ${AWS_CLOUDFRONT_DISTRIBUTION_ID} --paths /*

echo "Done!"
