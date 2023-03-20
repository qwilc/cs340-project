#!/bin/bash
arr=(
        "getFollowing"
        "getFollowers"
        "getStory"
        "login"
        "getFeed"
        "getFollowingCount"
        "getFollowersCount"
        "getUser"
        "isFollower"
        "logout"
        "register"
        "postStatus"
        "follow"
        "unfollow"
    )
for FUNCTION_NAME in "${arr[@]}"
do
  aws lambda update-function-code --function-name $FUNCTION_NAME --zip-file fileb:///C:/Users/qgwil/340/Tweeter/server/build/libs/server-all.zip &
done