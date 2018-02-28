#!/bin/bash

if [ "$MATRIX_JOB_TYPE" == "TEST" ]; then
    while sleep 9m; do echo -e '\n=====[ Gradle build is still running ]====='; done &

    echo "Pulling Couchbase docker image..."
    docker pull couchbase/server:4.6.4

    echo "Running Couchbase docker image..."
    docker run -d --name couchbase -p 8091-8094:8091-8094 -p 11210:11210 couchbase/server:5.1.0

    docker ps | grep "couchbase"
    retVal=$?
    if [ $retVal == 0 ]; then
        echo "Couchbase docker image is running."
    else
        echo "Couchbase docker image failed to start."
        exit $retVal
    fi

    echo "Waiting for Couchbase server to come online..."
    until $(curl --output /dev/null --silent --head --fail http://localhost:8091); do
        printf '.'
        sleep 1
    done

    echo -e "\nCreating testbucket Couchbase bucket..."
    curl -X POST -d 'name=testbucket' -d 'bucketType=couchbase' -d 'ramQuotaMB=120' -d 'authType=none' -d 'proxyPort=11216' http://localhost:8091/pools/default/buckets

    curl -X PUT --data "roles=bucket_admin[testbucket]&password=password" \
                 -H "Content-Type: application/x-www-form-urlencoded" \
                 http://Administrator:password@127.0.0.1:8091/settings/rbac/users/local/testbucket

    echo -e "\nCreating casbucket Couchbase bucket..."
    curl -X POST -d name=casbucket -d bucketType=couchbase -d ramQuotaMB=120 -d authType=none -d proxyPort=11217 http://localhost:8091/pools/default/buckets

    curl http://localhost:8091/pools/default/buckets
fi
